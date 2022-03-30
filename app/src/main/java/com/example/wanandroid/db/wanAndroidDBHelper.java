package com.example.wanandroid.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.wanandroid.activity.MainActivity;

public class wanAndroidDBHelper extends SQLiteOpenHelper {
    public static final String CREATE_ANDROID ="create table User("
            +"id integer primary key autoincrement,"
            +"userName text,"
            +"passWord text)";
    private Context mContext;
    public wanAndroidDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ANDROID);
        Toast.makeText(mContext,"Create User succeeded",Toast.LENGTH_SHORT).show();
        //MainActivity.mPref.edit().clear().apply();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists User");
        onCreate(sqLiteDatabase);
    }
}
