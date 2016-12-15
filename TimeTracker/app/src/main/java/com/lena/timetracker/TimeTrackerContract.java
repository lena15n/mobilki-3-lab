package com.lena.timetracker;

import android.provider.BaseColumns;

public final class TimeTrackerContract {
    //public static final String AUTHORITY = "com.lena.TransitProvider"; - if I use contentprovider
    public static final String SCHEME = "content://";
    public static final String SLASH = "/";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TimeTracker.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";



    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TimeTrackerContract() {}

    /* Inner class that defines the table contents */
    public static final class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";

        protected static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TimeTrackerContract.FeedEntry.TABLE_NAME + " (" +
                        TimeTrackerContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        TimeTrackerContract.FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        TimeTrackerContract.FeedEntry.COLUMN_NAME_SUBTITLE + TEXT_TYPE + " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TimeTrackerContract.FeedEntry.TABLE_NAME;
    }

    public static final class Category implements BaseColumns {
        public static final String TABLE_NAME       = "category";
        public static final String NAME = "name";

        public static final String SQL_CREATE_CATEGORY = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                NAME + TEXT_TYPE + " )";
        public static final String SQL_DELETE_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Category (){}
    }

    public static final class Record implements BaseColumns {
        public static final String TABLE_NAME       = "record";
        public static final String DESCRIPTION = "description";
        public static final String START_TIME = "start";
        public static final String END_TIME = "end";
        public static final String CATEGORY = "category";
        public static final String TIME = "time";
        public static final String PHOTO = "photo";

        public static final String SQL_CREATE_RECORD = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                PHOTO + TEXT_TYPE + COMMA_SEP +
                START_TIME + TEXT_TYPE + COMMA_SEP +
                END_TIME + TEXT_TYPE + COMMA_SEP +
                CATEGORY + TEXT_TYPE + COMMA_SEP +
                TIME + TEXT_TYPE + " )";
        public static final String SQL_DELETE_RECORD = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Record (){}
    }

    public static final class Photo implements BaseColumns {
        public static final String TABLE_NAME      = "photo";
        public static final String URI = "uri";
        public static final String NAME = "name";

        public static final String SQL_CREATE_PHOTO = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                URI + TEXT_TYPE + COMMA_SEP +
                NAME + TEXT_TYPE + " )";
        public static final String SQL_DELETE_PHOTO = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Photo (){}
    }


}