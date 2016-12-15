package com.lena.timetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.lena.timetracker.TimeTrackerContract.Category.SQL_CREATE_CATEGORY;
import static com.lena.timetracker.TimeTrackerContract.Category.SQL_DELETE_CATEGORY;
import static com.lena.timetracker.TimeTrackerContract.DATABASE_NAME;
import static com.lena.timetracker.TimeTrackerContract.DATABASE_VERSION;
import static com.lena.timetracker.TimeTrackerContract.FeedEntry.SQL_CREATE_ENTRIES;
import static com.lena.timetracker.TimeTrackerContract.Photo.SQL_CREATE_PHOTO;
import static com.lena.timetracker.TimeTrackerContract.Photo.SQL_DELETE_PHOTO;
import static com.lena.timetracker.TimeTrackerContract.Record.SQL_CREATE_RECORD;
import static com.lena.timetracker.TimeTrackerContract.Record.SQL_DELETE_RECORD;

public class TimeTrackerDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.


    public TimeTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_CATEGORY);
        db.execSQL(SQL_CREATE_RECORD);
        db.execSQL(SQL_CREATE_PHOTO);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_CATEGORY);
        db.execSQL(SQL_DELETE_RECORD);
        db.execSQL(SQL_DELETE_PHOTO);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}