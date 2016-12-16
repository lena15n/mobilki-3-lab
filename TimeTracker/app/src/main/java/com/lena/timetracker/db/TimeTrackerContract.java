package com.lena.timetracker.db;

import android.provider.BaseColumns;

public final class TimeTrackerContract {
    //public static final String AUTHORITY = "com.lena.TransitProvider"; - if I use contentprovider
    public static final String SCHEME = "content://";
    public static final String SLASH = "/";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TimeTracker.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";




    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TimeTrackerContract() {}

    public static final class Category implements BaseColumns {
        public static final String TABLE_NAME       = "category";
        public static final String NAME = "name";
        public static final String BASIC_WORK = "Work";
        public static final String BASIC_DINNER = "Dinner";
        public static final String BASIC_REST = "Rest";
        public static final String BASIC_CLEANING = "Cleaning";
        public static final String BASIC_SLEEP = "Sleep";


        public static final String SQL_CREATE_CATEGORY = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                NAME + TEXT_TYPE + " )";
        public static final String SQL_DELETE_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String SQL_INSERT_BASIC_CATEGORY_WORK = "INSERT INTO " + TABLE_NAME +
                " (" + NAME + ")" + " VALUES ('" + BASIC_WORK + "'); \n";
        public static final String SQL_INSERT_BASIC_CATEGORY_DINNER = "INSERT INTO " + TABLE_NAME +
                " (" + NAME + ")" + " VALUES ('" + BASIC_DINNER + "'); \n";
        public static final String SQL_INSERT_BASIC_CATEGORY_REST = "INSERT INTO " + TABLE_NAME +
                " (" + NAME + ")" + " VALUES ('" + BASIC_REST + "'); \n";
        public static final String SQL_INSERT_BASIC_CATEGORY_CLEANING = "INSERT INTO " + TABLE_NAME +
                " (" + NAME + ")" + " VALUES ('" + BASIC_CLEANING + "'); \n";
        public static final String SQL_INSERT_BASIC_CATEGORY_SLEEP = "INSERT INTO " + TABLE_NAME +
                " (" + NAME + ")" + " VALUES ('" + BASIC_SLEEP + "'); \n";



        private Category (){}
    }

    public static final class Record implements BaseColumns {
        public static final String TABLE_NAME       = "record";
        public static final String DESCRIPTION = "description";
        public static final String START_TIME = "start";
        public static final String END_TIME = "end";
        public static final String CATEGORY_ID = "category";
        public static final String TIME = "time";

        private static final String TEMP_TABLE_NAME = "temp";

        public static final String SQL_CREATE_RECORD = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                START_TIME + TEXT_TYPE + COMMA_SEP +
                END_TIME + TEXT_TYPE + COMMA_SEP +
                CATEGORY_ID + INTEGER_TYPE + COMMA_SEP +
                TIME + INTEGER_TYPE + " )";

        public static final String SQL_LEFT_JOIN_PHOTO_THEN_CATEGORIES = "SELECT * FROM (" +
                "SELECT * FROM " + TABLE_NAME + " LEFT JOIN " + Photo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _ID + " = " + Photo.TABLE_NAME + "." + Photo.RECORD_ID +
                ") AS " + TEMP_TABLE_NAME +
                " LEFT JOIN " + Category.TABLE_NAME +
                " ON " + TEMP_TABLE_NAME  + "." + CATEGORY_ID + " = " + Category.TABLE_NAME + "." + Category._ID;

        public static final String SQL_DELETE_RECORD = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Record (){}
    }

    public static final class Photo implements BaseColumns {
        public static final String TABLE_NAME      = "photo";
        public static final String URI = "uri";
        public static final String RECORD_ID = "name";

        public static final String SQL_CREATE_PHOTO = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                URI + TEXT_TYPE + COMMA_SEP +
                RECORD_ID + INTEGER_TYPE + " )";
        public static final String SQL_DELETE_PHOTO = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Photo (){}
    }


}