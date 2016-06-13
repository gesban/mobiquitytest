package com.example.mobiquitytest.network;

import android.content.Context;

import com.example.mobiquitytest.entities.Result;
import com.example.mobiquitytest.utils.MetadataHelper;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jonathan Gama on 6/11/16.
 */
public class RetrofitHelper {
    private static final String BASE_URL = "http://api.openweathermap.org/";
    private static final String METADATA_WEATHER_KEY = "OPEN_WEATHER_API_KEY";

    public static Retrofit buildRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Call<Result> buildWeatherCall(Context context, Double latitude, Double longitude) {
        Retrofit retrofit = buildRetrofitInstance();
        WeatherService weatherService = retrofit.create(WeatherService.class);
        return weatherService.retrieveCurrent(MetadataHelper.getValue(context, METADATA_WEATHER_KEY), latitude, longitude);
    }

    public static Call<Result> buildForecastCall(Context context, Double latitude, Double longitude) {
        Retrofit retrofit = buildRetrofitInstance();
        WeatherService weatherService = retrofit.create(WeatherService.class);
        return weatherService.retrieveCurrent(MetadataHelper.getValue(context, METADATA_WEATHER_KEY), latitude, longitude);
    }
}
