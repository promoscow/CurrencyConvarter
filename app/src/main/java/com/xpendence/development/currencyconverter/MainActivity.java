package com.xpendence.development.currencyconverter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xpendence.development.currencyconverter.data.CurrenciesDBHelper;
import com.xpendence.development.currencyconverter.operations.Converter;
import com.xpendence.development.currencyconverter.operations.Currency;

import java.util.Map;

import static com.xpendence.development.currencyconverter.HomeActivity.isReallyOnline;

/**
 * Применённые технологии и библиотеки:
 * 1. SQLite
 * 2. SwipeRefreshLayout
 * 3. Jsoup
 */
public class MainActivity extends AppCompatActivity {
    public static Converter converter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        CurrenciesDBHelper dbHelper = new CurrenciesDBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        converter = new Converter(database);
        prepareDbAndCurrencies(database);
    }

    public void prepareDbAndCurrencies(SQLiteDatabase database) {
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                if (c.getString(0).equals("currencies")) {
                    Log.d("prepareDbAndCurrencies1", "equals currencies");
                    Map<String, Currency> map = converter.getCurrencies(database);
                    if (map != null && map.size() != 0) {
                        Log.d("prepareDbAndCurrencies2", "map!=null");
                        Toast.makeText(getBaseContext(), getString(R.string.downloaded) + " " + map.size(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("database", "exists");
                        c.close();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }, 1000);
                        return;
                    }
                }
                c.moveToNext();
            }
        }
        c.close();
        Log.d("prepareDbAndCurrencies3", "writeCToDB");
        writeCurrenciesToDB();
    }

    public void writeCurrenciesToDB() {
        if (!isReallyOnline()) {
            converter.getDemoCurrenciesWrap();
            Toast.makeText(getBaseContext(),
                    R.string.in_demo,
                    Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, HintsActivity.class);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        try {
            Intent intent = new Intent(MainActivity.this, HintsActivity.class);
            startActivity(intent);
            converter.prepareDB();
            Toast.makeText(getBaseContext(), R.string.update_successfull,
                    Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void backToHome(View view) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
