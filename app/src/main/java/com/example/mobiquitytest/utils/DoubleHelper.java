package com.example.mobiquitytest.utils;

import java.math.BigDecimal;

/**
 * Created by Jonathan Gama on 6/12/16.
 */
public class DoubleHelper {
    public static Double truncateDouble(Double number, int precision) {
        return new BigDecimal(number)
                .setScale(precision, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
