package com.cloud.cms.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String RESOURCE_TB_NAME = "file_manager";
    public static final String DOWNLOAD_TASK_TB_NAME = "download_manager";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("test", "create table");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+RESOURCE_TB_NAME+" (id integer primary key autoincrement,date varchar,url varchar,wholeurl varchar,name varchar,state integer,complete integer)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+DOWNLOAD_TASK_TB_NAME+" (id integer primary key autoincrement,messageid varchar,complete integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + RESOURCE_TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DOWNLOAD_TASK_TB_NAME);
        onCreate(db);
    }

}

