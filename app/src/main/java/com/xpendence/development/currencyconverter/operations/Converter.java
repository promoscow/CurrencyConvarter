package com.xpendence.development.currencyconverter.operations;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.Map;

/**
 * Основной класс, типа Main.
 * Created by promoscow on 20.04.17.
 */

public class Converter {
    private SQLiteDatabase database;
    private Strategy strategy;
    private Map<String, Currency> currencies;

    public Converter(SQLiteDatabase database) {
        this.database = database;
        strategy = new Strategy();
    }

    public void process() {
    }

    public void prepareDB() {
//        ContentValues contentValues = new ContentValues();
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
        }
    }
}
