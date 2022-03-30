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
import com.example.wanandroid.db.wanAndroidDBHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText editAccount;
    private EditText editPassword;
    private EditText editOkPassword;
    private ImageView ivEye;  //定义控件
    private ImageView ivOkEye;
    private boolean isHide;
    private Button btnRegister;
    private int VERSION = 3;
    private wanAndroidDBHelper dbHelper;
    private SQLiteDatabase db;


    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        isHide = true;
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editAccount = (EditText) findViewById(R.id.edit_account);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editOkPassword = (EditText) findViewById(R.id.edit_ok_password);
        ivEye = (ImageView) findViewById(R.id.eye);
        ivOkEye = (ImageView) findViewById(R.id.ok_eye);
        btnRegister = (Button) findViewById(R.id.click_register);
        ivEye.setImageResource(R.drawable.close);   //选择初始样貌为闭眼
        ivOkEye.setImageResource(R.drawable.close);   //选择初始样貌为闭眼
        TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
        editPassword.setTransformationMethod(method);
        editOkPassword.setTransformationMethod(method);

        //Toolbar返回按钮点击事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置密码 眼睛图片点击事件
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

        //确认密码 眼睛图片点击事件
        ivOkEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok_eye:
                        if (isHide == true) {
                            ivOkEye.setImageResource(R.drawable.open);  //可见样貌
                            HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance(); //可见
                            editOkPassword.setTransformationMethod(method);
                            isHide = false;
                        } else {
                            ivOkEye.setImageResource(R.drawable.close); //不可见样貌
                            TransformationMethod method = PasswordTransformationMethod.getInstance();  //隐藏
                            editOkPassword.setTransformationMethod(method);
                            isHide = true;
                        }
                        int index = editOkPassword.getText().toString().length();
                        editOkPassword.setSelection(index);
                        break;
                }
            }
        });

        //注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = editAccount.getText().toString();
                String password = editPassword.getText().toString();
                String ok_password = editOkPassword.getText().toString();
                String telRegex = "^[a-zA-Z0-9_-]{4,16}$";
                //用户名、设置密码、确认密码有一个为空，则提示完善注册信息
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || TextUtils.isEmpty(ok_password)) {
                    Toast.makeText(RegisterActivity.this, "请完善注册信息", Toast.LENGTH_SHORT).show();
                    editAccount.setText("");
                    editPassword.setText("");
                    editOkPassword.setText("");
                    editAccount.requestFocus();
                    editAccount.setSelection(0);
                    //三个编辑框都不为空，且输入的两次密码一致，此时查看密码格式是否符合正则表达式要求
                } else if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(ok_password) && password.equals(ok_password)) {
                    if (!password.matches(telRegex)) {
                        Toast.makeText(RegisterActivity.this, "注册密码请输入4到16位 ", Toast.LENGTH_SHORT).show();
                        editPassword.setText("");
                        editOkPassword.setText("");
                        editPassword.requestFocus();
                        editPassword.setSelection(0);
                        //查看用户名是否存在 存在提示用户名已存在，不存在提示注册成功
                    } else {
                        dbHelper = new wanAndroidDBHelper(RegisterActivity.this, "wanAndroid.db", null, VERSION);
                        db = dbHelper.getWritableDatabase();
                        Cursor cursor = db.rawQuery("select * from User where userName=?", new String[]{account});
                        if (cursor.moveToFirst()) {
                            do {
                                String userName = cursor.getString(cursor.getColumnIndex("userName"));
                                Toast.makeText(RegisterActivity.this, "用户名 " + userName + " 已存在", Toast.LENGTH_SHORT).show();
                            } while (cursor.moveToNext());
                            cursor.close();
                            editAccount.setText("");
                            editPassword.setText("");
                            editOkPassword.setText("");
                            editAccount.requestFocus();
                            editAccount.setSelection(0);
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("userName", account);
                            contentValues.put("passWord", password);
                            db.insert("User", null, contentValues);
                            contentValues.clear();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    }
                    //三个编辑框都不为空，且两次密码不一致，则提示用户输入两次密码不一致
                } else if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(ok_password) && !password.equals(ok_password)) {
                    Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    editAccount.setText("");
                    editPassword.setText("");
                    editOkPassword.setText("");
                    editAccount.requestFocus();
                    editAccount.setSelection(0);
                }
            }
        });
    }
}