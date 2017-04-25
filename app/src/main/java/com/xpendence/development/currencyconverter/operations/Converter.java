package com.xpendence.development.currencyconverter.operations;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.xpendence.development.currencyconverter.data.CurrenciesContract;

import java.util.Map;

/**
 * Основной класс, типа Main.
 * Created by promoscow on 20.04.17.
 */

public class Converter {
    private SQLiteDatabase database;
    public static Map<String, Currency> currencies;

    public Converter(SQLiteDatabase database) {
        this.database = database;
    }

    public void process() {
    }

    public void prepareDB() throws InterruptedException {
        Cursor cursor = database.query("currencies", null, null, null, null, null, null);
        Log.d("db", cursor.toString());
//        if (database.execSQL("SELECT * FROM sqlite_master WHERE TYPE = 'table")) ;
        new DBConnection().execute();
    }

    private class DBConnection extends AsyncTask<Void, Void, Map<String, Currency>> {

        @Override
        protected Map<String, Currency> doInBackground(Void... voids) {
            return new Strategy().getCurrencies();
        }

        @Override
        protected void onPostExecute(Map<String, Currency> map) {
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
