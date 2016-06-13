package com.example.mobiquitytest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.mobiquitytest.R;

/**
 * Created by Jonathan Gama on 6/12/16.
 */
public class SharedPreferencesHelper {
    private static final String UNIT_STRING_KEY = "TEMP_UNIT_KEY";

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isInCelsius(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String tmpString = getUnitString(context, sharedPreferences);
        return tmpString.equals(context.getString(R.string.m_units_celsius));
    }

    private static String getUnitString(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(UNIT_STRING_KEY, context.getString(R.string.m_units_celsius));
    }

    public static boolean changeUnit(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isInCelsius(context)) {
            editor.putString(UNIT_STRING_KEY, context.getString(R.string.m_units_fahrenheit));
        } else {
            editor.putString(UNIT_STRING_KEY, context.getString(R.string.m_units_celsius));
        }
        editor.apply();
        return isInCelsius(context);
    }
}
