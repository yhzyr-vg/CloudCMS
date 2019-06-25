package com.cloud.cms.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {

    String TAG = "DatabaseHelper";

    private static String DB_NAME = "victgroup_tvdms.db";

    private static int DB_VERSION = 1;

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private Cursor cursor;

    Context context;
    static DatabaseHelper databaseHelper;

    public static synchronized void init(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
    }

    public synchronized static DatabaseHelper getInstance() {
        if (databaseHelper == null) {
            throw new RuntimeException("please init first!");
        }
        return databaseHelper;
    }

    private DatabaseHelper(Context context) {
        this.context = context;
        dbHelper = new SQLiteHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {

        return db.insert(table, nullColumnHack, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {

        return db.update(table, values, whereClause, whereArgs);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy) {

        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {

        return db.delete(table, whereClause, whereArgs);
    }

}

