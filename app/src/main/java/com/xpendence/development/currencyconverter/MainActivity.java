package com.xpendence.development.currencyconverter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xpendence.development.currencyconverter.data.CurrenciesDBHelper;
import com.xpendence.development.currencyconverter.operations.Converter;
import com.xpendence.development.currencyconverter.operations.Currency;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static Converter converter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /**
         * Здесь происходит создание базы данных и подгрузка ресурсов, пользователь пока видит логотип.
         * Как только ресурсы загружены, пользователь переходит на следующий экран, он же главный.
         */
        CurrenciesDBHelper dbHelper = new CurrenciesDBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        converter = new Converter(database);
        prepareDbAndCurrencies(database, dbHelper);

        /**
         * Если база данных существует, тогда — другой метод, наполнение объектами из БД.
         */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }

    public void prepareDbAndCurrencies(SQLiteDatabase database, CurrenciesDBHelper dbHelper) {
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                if (c.getString(0).equals("currencies")) {
                    Map<String, Currency> map = converter.getCurrencies(database);
                    if (map != null && map.size() != 0) {
                        Toast.makeText(getBaseContext(), "Загружено валют: " + map.size(),
                                Toast.LENGTH_LONG).show();
                    }
                    Log.d("database", "exists");
                    return;
                }
                c.moveToNext();
            }
        }
        writeCurrenciesToDB();
        c.close();
    }

    public void writeCurrenciesToDB() {

        try {
            converter.prepareDB();
            Toast.makeText(getBaseContext(), "Курсы валют успешно обновлены", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
