package com.xpendence.development.currencyconverter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xpendence.development.currencyconverter.data.CurrenciesContract;
import com.xpendence.development.currencyconverter.data.CurrenciesDBHelper;
import com.xpendence.development.currencyconverter.operations.Converter;
import com.xpendence.development.currencyconverter.operations.Currency;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static Converter converter;
//    public final String CONVERTER = "converter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /**
         * Здесь происходит создание базы данных и подгрузка ресурсов, пользователь пока видит логотип.
         * Как только ресурсы загружены, пользователь переходит на следующий экран, он же главный.
         */


        CurrenciesDBHelper dbHelper = new CurrenciesDBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        converter = new Converter(database);
        try {
            converter.prepareDB();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.put
//        super.onSaveInstanceState(savedInstanceState);
//    }
}
