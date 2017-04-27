package com.xpendence.development.currencyconverter;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xpendence.development.currencyconverter.operations.Converter;

public class HintsActivity extends AppCompatActivity {
    Converter converter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hints);

        converter = (Converter) getIntent().getSerializableExtra(SyncStateContract.Constants.DATA);
        Log.d("hint activity", converter.toString());
    }
}
