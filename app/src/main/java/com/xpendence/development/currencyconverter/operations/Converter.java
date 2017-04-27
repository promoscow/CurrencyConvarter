package com.xpendence.development.currencyconverter.operations;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.xpendence.development.currencyconverter.data.CurrenciesContract;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Основной класс, типа Main.
 * Created by promoscow on 20.04.17.
 */

public class Converter {
    private SQLiteDatabase database;
    public static Map<String, Currency> currencies;

    public static Map<String, Currency> getCurrencies() {
        return currencies;
    }

    public Converter(SQLiteDatabase database) {
        this.database = database;
    }

    public void prepareDB() throws InterruptedException {
        Log.d("method invoke", "prepareDB");
        new DBConnection().execute();
    }

    public Map<String, Currency> readCurrenciesFromDB(SQLiteDatabase database) {
        Map<String, Currency> map = new HashMap<>();
        Cursor cursor = database.query(CurrenciesContract.Currencies.TABLE_NAME,
                null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int dCodeIndex = cursor.getColumnIndex(CurrenciesContract.Currencies.D_CODE);
            int codeIndex = cursor.getColumnIndex(CurrenciesContract.Currencies.CODE);
            int forAmountIndex = cursor.getColumnIndex(CurrenciesContract.Currencies.FOR_AMOUNT);
            int rateIndex = cursor.getColumnIndex(CurrenciesContract.Currencies.RATE);
            int dateIndex = cursor.getColumnIndex(CurrenciesContract.Currencies.DATE);
            do {
                Currency currency = new Currency(cursor.getString(dCodeIndex),
                        cursor.getString(codeIndex),
                        cursor.getInt(forAmountIndex),
                        cursor.getDouble(rateIndex),
                        cursor.getString(dateIndex));
                Log.d("read from DB", currency.toString());
                Log.d("currency", currency.getCode());

                map.put(currency.getCode(), currency);
            } while (cursor.moveToNext());
        } else {
            Log.d("currenciesDB", "0 rows");
        }
        cursor.close();
        return map;
    }

    public Map<String, Currency> getCurrencies(SQLiteDatabase database) {
        return currencies = readCurrenciesFromDB(database);
    }

    private class DBConnection extends AsyncTask<Void, Void, Map<String, Currency>> {

        @Override
        protected Map<String, Currency> doInBackground(Void... voids) {
            Log.d("method invoke", "doInBackground");
            return new Strategy().getCurrencies();
        }

        @Override
        protected void onPostExecute(Map<String, Currency> map) {
            Log.d("method invoke", "onPostExecute, map");
            currencies = map;
            if (currencies != null && currencies.size() != 0) {
                database.delete(CurrenciesContract.Currencies.TABLE_NAME, null, null);
                for (Currency currency : currencies.values()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CurrenciesContract.Currencies.D_CODE, currency.getdCode());
                    contentValues.put(CurrenciesContract.Currencies.CODE, currency.getCode());
                    contentValues.put(CurrenciesContract.Currencies.FOR_AMOUNT, currency.getForAmount());
                    contentValues.put(CurrenciesContract.Currencies.RATE, currency.getRate());
                    contentValues.put(CurrenciesContract.Currencies.DATE, currency.getDate());

                    Log.d("ready to write DB", contentValues.toString());
                    database.insert(CurrenciesContract.Currencies.TABLE_NAME, null, contentValues);
                }
                Log.d("process", "DB fill, size: " + currencies.size());
            }
        }
    }

    public void getDemoCurrenciesWrap() {
        currencies = getDemoCurrencies();
    }

    private Map<String, Currency> getDemoCurrencies() {
        Map<String, Currency> map = new HashMap<>();
        map.put("AMD", new Currency("051", "AMD", 100, 11.7587, "28.04.2017"));
        map.put("JPY", new Currency("392", "JPY", 100, 51.1659, "28.04.2017"));
        map.put("CNY", new Currency("156", "CNY", 10, 82.6597, "28.04.2017"));
        map.put("KZT", new Currency("398", "KZT", 100, 18.1412, "28.04.2017"));
        map.put("AUD", new Currency("036", "AUD", 1, 42.6141, "28.04.2017"));
        map.put("KRW", new Currency("410", "KRW", 1000, 50.2589, "28.04.2017"));
        map.put("MDL", new Currency("498", "MDL", 10, 29.6221, "28.04.2017"));
        map.put("INR", new Currency("356", "INR", 100, 88.8709, "28.04.2017"));
        map.put("SEK", new Currency("752", "SEK", 10, 64.7174, "28.04.2017"));
        map.put("TJS", new Currency("972", "TJS", 10, 64.556, "28.04.2017"));
        map.put("NOK", new Currency("578", "NOK", 10, 66.6597, "28.04.2017"));
        map.put("ZAR", new Currency("710", "ZAR", 10, 42.9277, "28.04.2017"));
        map.put("BGN", new Currency("975", "BGN", 1, 31.7881, "28.04.2017"));
        map.put("CHF", new Currency("756", "CHF", 1, 57.3665, "28.04.2017"));
        map.put("BYN", new Currency("933", "BYN", 1, 30.4249, "28.04.2017"));
        map.put("EUR", new Currency("978", "EUR", 1, 62.1664, "28.04.2017"));
        map.put("KGS", new Currency("417", "KGS", 100, 84.8626, "28.04.2017"));
        map.put("TMT", new Currency("934", "TMT", 1, 16.303, "28.04.2017"));
        map.put("UAH", new Currency("980", "UAH", 10, 21.4579, "28.04.2017"));
        map.put("UZS", new Currency("860", "UZS", 1000, 15.3684, "28.04.2017"));
        map.put("CAD", new Currency("124", "CAD", 1, 41.9766, "28.04.2017"));
        map.put("HKD", new Currency("344", "HKD", 10, 73.2205, "28.04.2017"));
        map.put("XDR", new Currency("960", "XDR", 1, 77.9604, "28.04.2017"));
        map.put("USD", new Currency("840", "USD", 1, 56.9707, "28.04.2017"));
        map.put("CZK", new Currency("203", "CZK", 10, 23.0632, "28.04.2017"));
        map.put("RUB", new Currency("643", "RUB", 1, 1.0, "28.04.2017"));
        map.put("PLN", new Currency("985", "PLN", 1, 14.7029, "28.04.2017"));
        map.put("BRL", new Currency("986", "BRL", 1, 17.9577, "28.04.2017"));
        map.put("HUF", new Currency("348", "HUF", 100, 19.9017, "28.04.2017"));
        map.put("AZN", new Currency("944", "AZN", 1, 33.5122, "28.04.2017"));
        map.put("SGD", new Currency("702", "SGD", 1, 40.8041, "28.04.2017"));
        map.put("DKK", new Currency("208", "DKK", 10, 83.5727, "28.04.2017"));
        map.put("RON", new Currency("946", "RON", 1, 13.7309, "28.04.2017"));
        map.put("GBP", new Currency("826", "GBP", 1, 73.458, "28.04.2017"));
        map.put("TRY", new Currency("949", "TRY", 1, 16.0066, "28.04.2017"));
        return map;
    }
}
