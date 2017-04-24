package com.xpendence.development.currencyconverter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Помощник создания и обновления базы данных валют.
 * Created by promoscow on 20.04.17.
 */

public class CurrenciesDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CurrenciesDBHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "currenciesDb";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор {@link CurrenciesDBHelper}.
     *
     * @param context Контекст приложения
     */
    public CurrenciesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_CURRENCIES_TABLE = "CREATE TABLE " + CurrenciesContract.Currencies.TABLE_NAME + " ("
                + CurrenciesContract.Currencies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CurrenciesContract.Currencies.D_CODE + " TEXT NOT NULL, "
                + CurrenciesContract.Currencies.CODE + " TEXT NOT NULL, "
                + CurrenciesContract.Currencies.FOR_AMOUNT + " TEXT NOT NULL DEFAULT 1, "
                + CurrenciesContract.Currencies.RATE + " TEXT NOT NULL DEFAULT 1, "
                + CurrenciesContract.Currencies.DATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_CURRENCIES_TABLE);
    }

    /**
     * Если есть новая версия (отличная от той, DATABASE_VERSION, то выполняется этот метод:
     * удаляется старая база данных, создаётся новая
     * @param sqLiteDatabase база данных
     * @param oldVersion старая версия программы
     * @param newVersion новая версия программы
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CurrenciesContract.Currencies.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
