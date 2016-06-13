package com.example.mobiquitytest.utils;

import java.util.Date;

/**
 * Created by gesban on 6/13/2016.
 */
public class GlobalVariables {

    private int fragmentoActivoIndice=0;
    private String placeTemperature="";
    private String placeName="";


    private GlobalVariables() {

    }

    private static GlobalVariables instance;

    public static GlobalVariables getInstance() {
        if (instance == null)
            instance = new GlobalVariables();
        return instance;
    }

    public int getFragmentoActivoIndice() {
        return fragmentoActivoIndice;
    }

    public void setFragmentoActivoIndice(int fragmentoActivoIndice) {
        this.fragmentoActivoIndice = fragmentoActivoIndice;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceTemperature() {
        return placeTemperature;
    }

    public void setPlaceTemperature(String placeTemperature) {
        this.placeTemperature = placeTemperature;
    }
}
