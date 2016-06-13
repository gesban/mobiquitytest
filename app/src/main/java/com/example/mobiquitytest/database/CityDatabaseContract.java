package com.example.mobiquitytest.database;

import android.provider.BaseColumns;

/**
 * Created by Jonathan Gama on 6/12/16.
 */
public class CityDatabaseContract {

    public static class MarkerEntry implements BaseColumns {
        public static final String TABLE_NAME = "markers";

        public static final String KEY_NAME = "name";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_LATITUDE = "latitude";
        public static final String KEY_LONGITUDE = "longitude";
        public static final String KEY_TEMP = "temp";
        public static final String KEY_MAX = "max";
        public static final String KEY_MIN = "min";

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_NAME + " TEXT," +
                KEY_ADDRESS + " TEXT," +
                KEY_LATITUDE + " REAL," +
                KEY_LONGITUDE + " REAL," +
                KEY_TEMP + " REAL," +
                KEY_MAX + " REAL," +
                KEY_MIN + " REAL," +
                " UNIQUE (" + KEY_LATITUDE + ", " +
                KEY_LONGITUDE + ") ON CONFLICT REPLACE);";
    }
}
