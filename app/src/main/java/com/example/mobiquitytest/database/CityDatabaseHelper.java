package com.example.mobiquitytest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonathan Gama on 6/12/16.
 */
public class CityDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "markerDB";
    private static final int DATABASE_VERSION = 3;

    public CityDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CityDatabaseContract.MarkerEntry.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CityDatabaseContract.MarkerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
