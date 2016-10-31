package com.friendlyarm.serial.seaweather4shd.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangxin on 2016/4/26.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_MSG = "create table msg( "
            + "id integer primary key autoincrement,"
            + "time text,"
            + "phoneNo text,"
            + "content text )";

    public static final String CREATE_BMSG = "create table bmsg( "
            + "id integer primary key autoincrement,"
            + "time text,"
            + "content text )";

    public static final String CREATE_WEATHER = "create table weather( "
            + "id integer primary key autoincrement,"
            + "time text,"
            + "content text )";
    
    public static final String CREATE_TYPHOON = "create table typhoon( "
            + "id integer primary key autoincrement,"
            + "typhoonNo interger,"
            + "locateX interger,"
            + "locateY interger )";
    
    //用来接受左边消息栏的内容
    public static final String CREATE_TMP_MSGLIST = "create table tmpMSG( "
            + "id integer primary key autoincrement,"
            + "img interger,"
            + "time text,"
            + "msg text )";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSG);
        db.execSQL(CREATE_BMSG);
        db.execSQL(CREATE_WEATHER);
        db.execSQL(CREATE_TYPHOON);
        db.execSQL(CREATE_TMP_MSGLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
