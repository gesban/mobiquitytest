package com.example.mobiquitytest.network;

import com.example.mobiquitytest.entities.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jonathan Gama on 6/11/16.
 */
public interface WeatherService {
    @GET("/data/2.5/weather")
    Call<Result> retrieveCurrent(@Query("appid") String appid, @Query("lat") Double lat, @Query("lon") Double lon);

    @GET("/data/2.5/forecast")
    Call<Result> retrieveForecast(@Query("appid") String appid, @Query("lat") Double lat, @Query("lon") Double lon);
}
