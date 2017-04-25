package com.xpendence.development.currencyconverter.operations;

import android.util.Log;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Вычисления валют
 * Created by promoscow on 21.04.17.
 */

public class Calculator {
    public static double calculate(Map<String, Currency> map, String currencyFirst, String currencySecond, int amount) throws IOException {

        Log.d("test", currencyFirst);
        Log.d("test", currencySecond);
        Currency currency1 = map.get(currencyFirst);
        Currency currency2 = map.get(currencySecond);
        Log.d("test", currency1.toString());
        Log.d("test", currency2.toString());

        double x = ((currency1.getRate() / currency1.getForAmount()) / (currency2.getRate() / currency2.getForAmount()) * amount);

        NumberFormat numberFormat = new DecimalFormat("0.00");
        numberFormat.setRoundingMode(RoundingMode.DOWN);

        Log.d("mLog", String.format("%d %s = %s %s",
                amount,
                currency1.getCode(),
                String.valueOf(numberFormat.format(x)),
                currency2.getCode()));

        return Double.parseDouble(String.valueOf(numberFormat.format(x)));
    }
}
