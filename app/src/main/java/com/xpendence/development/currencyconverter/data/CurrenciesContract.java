package com.xpendence.development.currencyconverter.data;

import android.provider.BaseColumns;

/**
 * Контракт-класс базы данных валют.
 * Created by promoscow on 20.04.17.
 */

public final class CurrenciesContract {


    public static final class Currencies implements BaseColumns {

        public static final String TABLE_NAME = "currencies";

        public static final String _ID = BaseColumns._ID;
        public static final String D_CODE = "dCode";
        public static final String CODE = "code";
        public static final String FOR_AMOUNT = "forAmount";
        public static final String RATE = "rate";
        public static final String DATE = "date";

    }
}
