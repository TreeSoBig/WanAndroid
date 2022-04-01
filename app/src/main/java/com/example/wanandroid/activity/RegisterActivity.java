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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wanandroid.R;
import com.example.wanandroid.db.WanAndroidDBHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEditAccount;
    private EditText mEditPassword;
    private EditText mEditOkPassword;
    private ImageView mIvEye;  //定义控件
    private ImageView mIvOkEye;
    private WanAndroidDBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private boolean isHide;
    private String mAccount;
    private String mPassword;
    private String mOkPassword;


    @SuppressLint("Range")
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

        //Toolbar返回按钮点击事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置密码 眼睛图片点击事件
        mIvEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.eye:
                        if (isHide == true) {
                            mIvEye.setImageResource(R.drawable.open);  //可见样貌
                            HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance(); //可见
                            mEditPassword.setTransformationMethod(method);
                            isHide = false;
                        } else {
                            mIvEye.setImageResource(R.drawable.close); //不可见样貌
                            TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
                            mEditPassword.setTransformationMethod(method);
                            isHide = true;
                        }
                        int index = mEditPassword.getText().toString().length();
                        mEditPassword.setSelection(index);
                        break;
                }
            }
        });

        //确认密码 眼睛图片点击事件
        mIvOkEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok_eye:
                        if (isHide == true) {
                            mIvOkEye.setImageResource(R.drawable.open);  //可见样貌
                            HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance(); //可见
                            mEditOkPassword.setTransformationMethod(method);
                            isHide = false;
                        } else {
                            mIvOkEye.setImageResource(R.drawable.close); //不可见样貌
                            TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
                            mEditOkPassword.setTransformationMethod(method);
                            isHide = true;
                        }
                        int index = mEditOkPassword.getText().toString().length();
                        mEditOkPassword.setSelection(index);
                        break;
                }
            }
        });

        //注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccount = mEditAccount.getText().toString();
                mPassword = mEditPassword.getText().toString();
                mOkPassword = mEditOkPassword.getText().toString();
                String telRegex = "^[a-zA-Z0-9_-]{4,16}$";
                //用户名、设置密码、确认密码有一个为空，则提示完善注册信息
                if (TextUtils.isEmpty(mAccount) || TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(mOkPassword)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.finish_register_info), Toast.LENGTH_SHORT).show();
                    setAccountFocus();
                    //三个编辑框都不为空，且输入的两次密码一致，此时查看密码格式是否符合正则表达式要求
                } else if (!TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mOkPassword) && mPassword.equals(mOkPassword)) {
                    if (!mPassword.matches(telRegex)) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.password_match), Toast.LENGTH_SHORT).show();
                        setPasswordFocus();
                        //查看用户名是否存在 存在提示用户名已存在，不存在提示注册成功
                    } else {
                        mDBHelper = WanAndroidDBHelper.getInstance(RegisterActivity.this);
                        mDB = mDBHelper.getWritableDatabase();
                        Cursor cursor = null;
                        try {
                            cursor = mDB.rawQuery("select * from User where userName=?", new String[]{mAccount});
                            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                                String userName = cursor.getString(cursor.getColumnIndex(WanAndroidDBHelper.USER_NAME));
                                Toast.makeText(RegisterActivity.this, userName + getString(R.string.username_exist), Toast.LENGTH_SHORT).show();
                                cursor.close();
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
                            cursor.close();
                        }
                    }
                    //三个编辑框都不为空，且两次密码不一致，则提示用户输入两次密码不一致
                } else if (!TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mOkPassword) && !mPassword.equals(mOkPassword)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.password_not_same), Toast.LENGTH_SHORT).show();
                    setPasswordFocus();
                }
            }
        });
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
}