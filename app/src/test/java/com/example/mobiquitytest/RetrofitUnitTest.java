package com.example.mobiquitytest;

import com.example.mobiquitytest.entities.Result;
import com.example.mobiquitytest.network.RetrofitHelper;
import com.example.mobiquitytest.network.WeatherService;

import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Jonathan Gama on 6/11/16.
 */
public class RetrofitUnitTest {
    @Test
    public void retrofit_retrievesWeatherInfo() throws Exception {
        Retrofit retrofit = RetrofitHelper.buildRetrofitInstance();
        WeatherService weatherService = retrofit.create(WeatherService.class);

        Call<Result> resultCall = weatherService.retrieveCurrent("3b535043693316ba125a0513276aa62d", 33.8921385, -84.4739904);
        Result result = resultCall.execute().body();
        System.out.println(result.getMain().getTempMax());
        System.out.println(result.getMain().getTempMin());
        System.out.println(result.getMain());

        assertNotNull(result);
    }
}
