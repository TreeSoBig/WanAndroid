package com.example.wanandroid.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wanandroid.R;
import com.example.wanandroid.db.WanAndroidDBHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static WanAndroidDBHelper mDBHelper;
    private final String REMEMBER_PASSWORD = "remember_password";
    private final String ACCOUNT = "account";
    private final String PASSWORD = "password";
    private EditText mEditAccount;
    private EditText mEditPassword;
    private SharedPreferences mUserPref;
    private SharedPreferences.Editor mEditor;
    private CheckBox mChkRememberPass;
    private ImageView mIvEye;
    private String mAccount;
    private String mPassword;
    private boolean isHide;

    //设置密码选项的眼睛图片
    public static void setEye(boolean isHide, ImageView mIvEye, EditText mEditPassword) {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isHide = true;
        mEditAccount = findViewById(R.id.edit_account);
        mEditPassword = findViewById(R.id.edit_password);
        mChkRememberPass = findViewById(R.id.remember_pass);
        mIvEye = findViewById(R.id.eye);//绑定控件
        mIvEye.setImageResource(R.drawable.close);   //选择初始样貌为闭眼
        TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
        mEditPassword.setTransformationMethod(method);
        mUserPref = PreferenceManager.getDefaultSharedPreferences(this);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        isHide = true;

        boolean isRemember = mUserPref.getBoolean(REMEMBER_PASSWORD, false);
        if (isRemember) {
            String account = mUserPref.getString(ACCOUNT, "");
            String password = mUserPref.getString(PASSWORD, "");
            mEditAccount.setText(account);
            mEditPassword.setText(password);
            mChkRememberPass.setChecked(true);
        }

        //设置页面返回按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置密码选项后面 眼睛图片的点击事件
        mIvEye.setOnClickListener(this);
        //登录按钮的点击事件
        btnLogin.setOnClickListener(this);
        // 注册按钮的点击事件
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.eye:
                setEye(isHide, mIvEye, mEditPassword);
                break;
            case R.id.btn_login:
                setLoginEvent();
                break;
            case R.id.btn_register: {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }

    //设置用户名光标位置
    private void setAccountFocus() {
        mEditAccount.setText("");
        mEditPassword.setText("");
        mEditAccount.requestFocus();
        mEditAccount.setSelection(0);
    }

    //设置记住密码时
    private void savePreferences() {
        mEditor = mUserPref.edit();
        if (mChkRememberPass.isChecked()) {
            mEditor.putBoolean(REMEMBER_PASSWORD, true);
            mEditor.putString(ACCOUNT, mAccount);
            mEditor.putString(PASSWORD, mPassword);
        } else {
            mEditor.clear();
        }
        mEditor.apply();
    }

    //设置登录按钮点击事件
    @SuppressLint("Range")
    private void setLoginEvent() {
        mAccount = mEditAccount.getText().toString();
        mPassword = mEditPassword.getText().toString();
        if (mAccount.isEmpty() || mPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, getString(R.string.finish_login_info), Toast.LENGTH_SHORT).show();
            setAccountFocus();
            return;
        }
        mDBHelper = WanAndroidDBHelper.getInstance(LoginActivity.this);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from User where userName=?", new String[]{mAccount});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                String qPassword = cursor.getString(cursor.getColumnIndex(WanAndroidDBHelper.PASSWORD));
                if (!qPassword.equals(mPassword)) {
                    //提示用户名或者密码不正确 清空内容
                    Toast.makeText(LoginActivity.this, getString(R.string.username_or_password_false), Toast.LENGTH_SHORT).show();
                    setAccountFocus();
                } else {
                    //密码与用户名匹配 登录成功
                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    savePreferences();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.USER_NAME_KEY, mAccount);
                    startActivity(intent);
                }
                return;
            }
            if (cursor != null) {
                Toast.makeText(LoginActivity.this, getString(R.string.username_not_exist), Toast.LENGTH_SHORT).show();
                setAccountFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null)
                cursor.close();
        }
    }
}