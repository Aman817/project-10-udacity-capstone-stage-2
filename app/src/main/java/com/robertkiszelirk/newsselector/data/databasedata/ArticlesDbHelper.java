package com.robertkiszelirk.newsselector.data.databasedata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ArticlesDbHelper extends SQLiteOpenHelper {

    // Constructor
    ArticlesDbHelper(Context context) {
        super(context, ArticlesContract.DATABASE_NAME, null, ArticlesContract.DATABASE_VERSION);
    }

    // Called when the articles database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ArticlesContract.ArticlesEntry.CREATE_TABLE_ARTICLES);
    }

    // This method discards the old table of data and calls onCreate to recreate a new one.
    // This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ArticlesContract.ArticlesEntry.DELETE_TABLE_ARTICLES);
        onCreate(db);
    }
}