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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wanandroid.R;
import com.example.wanandroid.db.wanAndroidDBHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText editAccount;
    private EditText editPassword;
    private Button btnLogin;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private CheckBox chkRememberPass;
    private Button btnRegister;
    private ImageView ivEye;
    private boolean isHide;
    private final int VERSION = 3;//数据库版本号
    private ImageView ivAvatar;
    private String imgInfo;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isHide = true;
        ivAvatar = (ImageView) findViewById(R.id.img_avatar);
        editAccount = (EditText) findViewById(R.id.edit_account);
        editPassword = (EditText) findViewById(R.id.edit_password);
        chkRememberPass = (CheckBox) findViewById(R.id.remember_pass);
        ivEye = (ImageView) findViewById(R.id.eye);//绑定控件
        ivEye.setImageResource(R.drawable.close);   //选择初始样貌为闭眼
        TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
        editPassword.setTransformationMethod(method);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        isHide = true;
        boolean isRemember = mPref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = mPref.getString("account", "");
            String password = mPref.getString("password", "");
            editAccount.setText(account);
            editPassword.setText(password);
            chkRememberPass.setChecked(true);
        }
        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.eye:
                        if (isHide == true) {
                            ivEye.setImageResource(R.drawable.open);  //可见样貌
                            HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance(); //可见
                            editPassword.setTransformationMethod(method);
                            isHide = false;
                        } else {
                            ivEye.setImageResource(R.drawable.close); //不可见样貌
                            TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
                            editPassword.setTransformationMethod(method);
                            isHide = true;
                        }
                        int index = editPassword.getText().toString().length();
                        editPassword.setSelection(index);
                        break;
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = editAccount.getText().toString();
                String password = editPassword.getText().toString();
                if (account.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请完善登录信息", Toast.LENGTH_SHORT).show();
                    editAccount.setText("");
                    editPassword.setText("");
                    editAccount.requestFocus();
                    editAccount.setSelection(0);
                } else {
                    wanAndroidDBHelper dbHelper = new wanAndroidDBHelper(LoginActivity.this, "wanAndroid.db", null, VERSION);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select * from User where userName=?", new String[]{account});
                    if (cursor.moveToFirst()) {
                        String qPassword = cursor.getString(cursor.getColumnIndex("passWord"));
                        if (!qPassword.equals(password)) {
                            //提示用户名或者密码不正确 清空内容
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            editAccount.setText("");
                            editPassword.setText("");
                            editAccount.requestFocus();
                            editAccount.setSelection(0);
                        } else {
                            //密码与用户名匹配 登录成功
                            //imgInfo = mPref.getString("imgInfo", "");
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            mEditor = mPref.edit();
                            if (chkRememberPass.isChecked()) {
                                mEditor.putBoolean("remember_password", true);
                                mEditor.putString("account", account);
                                mEditor.putString("password", password);
                            } else {
                                mEditor.clear();
                            }
                            mEditor.apply();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userName", account);
                            //intent.putExtra("imgInfo", imgInfo);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                        editAccount.setText("");
                        editPassword.setText("");
                        editAccount.requestFocus();
                        editAccount.setSelection(0);
                    }
                }

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}