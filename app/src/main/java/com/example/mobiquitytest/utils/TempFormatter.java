package com.example.mobiquitytest.utils;

import android.content.Context;
import android.util.Log;

import com.example.mobiquitytest.entities.Main;

import java.math.BigDecimal;

/**
 * Created by Jonathan Gama on 6/12/16.
 */
public class TempFormatter {

    private static final String TAG = "TempFormatter";

    public final static String CELSIUS_STRING = "°C";
    public final static String FAHRENHEIT_STRING = "°F";

    public static String formatTemp(Context context, Main main) {
        String scaleTemp = (SharedPreferencesHelper.isInCelsius(context))
                ? CELSIUS_STRING
                : FAHRENHEIT_STRING;

        Double[] formattedTemp = convertTemp(main, context);

        return formattedTemp[0] + scaleTemp +
                " Hi " +
                formattedTemp[1] +
                scaleTemp +
                " Lo " +
                formattedTemp[2] +
                scaleTemp;
    }

    private static Double[] convertTemp(Main main, Context context) {
        Double[] aux = new Double[3];
        if (SharedPreferencesHelper.isInCelsius(context)) {
            aux[0] = kelvinToCelsius(main.getTemp());
            aux[1] = kelvinToCelsius(main.getTempMax());
            aux[2] = kelvinToCelsius(main.getTempMin());
        } else {
            aux[0] = kelvinToFahrenheit(main.getTemp());
            aux[1] = kelvinToFahrenheit(main.getTempMax());
            aux[2] = kelvinToFahrenheit(main.getTempMin());
        }
        return aux;
    }

    private static Double kelvinToCelsius(Double temp) {
        return DoubleHelper.truncateDouble(temp - 273.15, 2);
    }

    private static Double kelvinToFahrenheit(Double temp) {
        return DoubleHelper.truncateDouble((temp * (9.0 / 5.0)) - 459.67, 2);
    }
}
