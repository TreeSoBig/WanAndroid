package com.example.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wanandroid.R;
import com.example.wanandroid.db.WanAndroidDBHelper;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditAccount;
    private EditText mEditPassword;
    private EditText mEditOkPassword;
    private ImageView mIvEye;  //定义控件
    private ImageView mIvOkEye;
    private SQLiteDatabase mDB;
    private boolean isHide;
    private String mAccount;
    private String mPassword;
    private String mOkPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        isHide = true;
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEditAccount = findViewById(R.id.edit_account);
        mEditPassword = findViewById(R.id.edit_password);
        mEditOkPassword = findViewById(R.id.edit_ok_password);
        mIvEye = findViewById(R.id.eye);
        mIvOkEye = findViewById(R.id.ok_eye);
        Button btnRegister = findViewById(R.id.click_register);
        mIvEye.setImageResource(R.drawable.close);   //选择初始样貌为闭眼
        mIvOkEye.setImageResource(R.drawable.close);   //选择初始样貌为闭眼
        TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
        mEditPassword.setTransformationMethod(method);
        mEditOkPassword.setTransformationMethod(method);

        mIvEye.setOnClickListener(this);
        mIvOkEye.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        //Toolbar返回按钮点击事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.eye:
                LoginActivity.setEye(isHide, mIvEye, mEditPassword);
                break;
            case R.id.ok_eye:
                LoginActivity.setEye(isHide, mIvOkEye, mEditOkPassword);
                break;
            case R.id.click_register:
                setRegisterEvent();
                break;
            default:
                break;
        }
    }

    //设置用户名编辑框光标位置
    private void setAccountFocus() {
        mEditAccount.setText("");
        mEditPassword.setText("");
        mEditOkPassword.setText("");
        mEditAccount.requestFocus();
        mEditAccount.setSelection(0);
    }

    //设置密码编辑框光标位置
    private void setPasswordFocus() {
        mEditPassword.setText("");
        mEditOkPassword.setText("");
        mEditPassword.requestFocus();
        mEditPassword.setSelection(0);
    }

    //插入用户数据到数据库
    private void insertSQLiteDatabase() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WanAndroidDBHelper.USER_NAME, mAccount);
        contentValues.put(WanAndroidDBHelper.PASSWORD, mPassword);
        mDB.insert(WanAndroidDBHelper.USER, null, contentValues);
        contentValues.clear();
    }

    //设置注册按钮点击事件
    @SuppressLint("Range")
    private void setRegisterEvent(){
        mAccount = mEditAccount.getText().toString();
        mPassword = mEditPassword.getText().toString();
        mOkPassword = mEditOkPassword.getText().toString();
        String telRegex = "^[a-zA-Z0-9_-]{4,16}$";
        //用户名、设置密码、确认密码有一个为空，则提示完善注册信息
        if (TextUtils.isEmpty(mAccount) || TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(mOkPassword)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.finish_register_info), Toast.LENGTH_SHORT).show();
            setAccountFocus();
            return;
        }
        //三个编辑框都不为空，且输入的两次密码一致，此时查看密码格式是否符合正则表达式要求
        if (!mPassword.equals(mOkPassword)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.password_not_same), Toast.LENGTH_SHORT).show();
            setPasswordFocus();
            return;
        }
        if (!mPassword.matches(telRegex)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.password_match), Toast.LENGTH_SHORT).show();
            setPasswordFocus();
            //查看用户名是否存在 存在提示用户名已存在，不存在提示注册成功
        } else {
            LoginActivity.mDBHelper = WanAndroidDBHelper.getInstance(RegisterActivity.this);
            mDB = LoginActivity.mDBHelper.getWritableDatabase();
            Cursor cursor = null;
            try {
                cursor = mDB.rawQuery("select * from User where userName=?", new String[]{mAccount});
                if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                    String userName = cursor.getString(cursor.getColumnIndex(WanAndroidDBHelper.USER_NAME));
                    Toast.makeText(RegisterActivity.this, userName + getString(R.string.username_exist), Toast.LENGTH_SHORT).show();
                    setAccountFocus();
                } else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    insertSQLiteDatabase();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(cursor != null)
                    cursor.close();
            }
        }
    }
}