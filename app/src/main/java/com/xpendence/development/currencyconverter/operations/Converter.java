package com.xpendence.development.currencyconverter.operations;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.xpendence.development.currencyconverter.data.CurrenciesContract;

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

    public void process() {
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
            Log.d("method invoke", "onPostExecute");
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

                    database.insert(CurrenciesContract.Currencies.TABLE_NAME, null, contentValues);
                }
                Log.d("process", "DB fill, size: " + currencies.size());
            }
        }
    }
}
