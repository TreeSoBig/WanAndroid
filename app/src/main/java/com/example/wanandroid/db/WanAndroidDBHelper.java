package com.example.wanandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.wanandroid.application.MyApplication;

public class WanAndroidDBHelper extends SQLiteOpenHelper {
    public static final String CREATE_ANDROID ="create table User("
            +"id integer primary key autoincrement,"
            +"userName text,"
            +"passWord text)";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "passWord";
    public static final String USER = "User";
    public static final int VERSION = 3;
    //private Context mContext;
    private static WanAndroidDBHelper wanAndroidDBHelper;
    private WanAndroidDBHelper(Context context){
        super(context,"wanAndroid.db",null,VERSION);
        //this.mContext = context;
    }

    public static WanAndroidDBHelper getInstance(Context context){
            if(wanAndroidDBHelper == null){
                wanAndroidDBHelper = new WanAndroidDBHelper(context);
            }
            return wanAndroidDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ANDROID);
        //Toast.makeText(mContext,"Create User succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists User");
        onCreate(sqLiteDatabase);
    }
}
