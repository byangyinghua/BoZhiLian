package cn.com.data_plus.bozhilian.database;

import android.database.sqlite.SQLiteDatabase;

import com.ly723.db.sdhelper.SQLiteSDCardHelper;

public class SDCardSQLiteHelper extends SQLiteSDCardHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "task.db";

    public SDCardSQLiteHelper(String path) {
        super(null, DATABASE_NAME, path, null, DATABASE_VERSION, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}