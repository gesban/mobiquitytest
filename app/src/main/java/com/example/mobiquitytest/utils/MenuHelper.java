package com.example.mobiquitytest.utils;

import android.content.Context;
import android.view.MenuItem;

import com.example.mobiquitytest.R;

/**
 * Created by Jonathan Gama on 6/12/16.
 */
public class MenuHelper {
    public static void setMenuItemTitle(Context context, MenuItem menuItem) {
        if (SharedPreferencesHelper.isInCelsius(context)) {
            menuItem.setTitle(context.getString(R.string.m_units) + context.getString(R.string.m_units_fahrenheit));
        } else {
            menuItem.setTitle(context.getString(R.string.m_units) + context.getString(R.string.m_units_celsius));
        }
    }
}
