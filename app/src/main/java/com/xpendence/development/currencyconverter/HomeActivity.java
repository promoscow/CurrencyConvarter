package com.xpendence.development.currencyconverter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xpendence.development.currencyconverter.operations.Calculator;
import com.xpendence.development.currencyconverter.operations.Converter;
import com.xpendence.development.currencyconverter.operations.Currency;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    Map<String, Currency> currencies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currencies = Converter.currencies;
        for (String s : currencies.keySet()) Log.d("currency", s);

        final String[] currenciesArray = new String[currencies.size()];
        int i = 0;
        for (Currency s : currencies.values()) currenciesArray[i++] = String.format("%s (%s)", s.getCode(), geCurrencyName(s));

        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        ArrayAdapter<String> arrayAdapterFrom = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, currenciesArray);
        arrayAdapterFrom.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(arrayAdapterFrom);
        int indexFrom = Arrays.asList(currenciesArray).indexOf("USD (Доллар США)");
        spinnerFrom.setSelection(indexFrom);

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ваш выбор: " + currenciesArray[selectedItemPosition], Toast.LENGTH_SHORT);
                toast.show();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        ArrayAdapter<String> arrayAdapterTo = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, currenciesArray);
        arrayAdapterTo.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerTo.setAdapter(arrayAdapterTo);
        int indexTo = Arrays.asList(currenciesArray).indexOf("RUB (Российский рубль)");
        spinnerTo.setSelection(indexTo);

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ваш выбор: " + currenciesArray[selectedItemPosition], Toast.LENGTH_SHORT);
                toast.show();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        double x = 0;
        try {
            x = Calculator.calculate(currencies, "840", "643", 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView currencyFrom = (TextView) findViewById(R.id.textViewFrom);
        String result1 = "1" + " USD";
        currencyFrom.setText(result1);

        TextView currencyTo = (TextView) findViewById(R.id.textViewTo);
        String result2 = x + " RUB";
        currencyTo.setText(result2);
    }



    private String geCurrencyName(Currency s) {
        switch (s.getCode()) {
            case "AUD" : return "Австралийский доллар";
            case "AZN" : return "Азербайджанский манат";
            case "AMD" : return "Армянский драм";
            case "BYN" : return "Белорусский рубль";
            case "BGN" : return "Болгарский лев";
            case "BRL" : return "Бразильский реал";
            case "HUF" : return "Венгерский форинт";
            case "KRW" : return "Южнокорейский вон";
            case "HKD" : return "Гонгконгский доллар";
            case "DKK" : return "Датская крона";
            case "USD" : return "Доллар США";
            case "EUR" : return "Евро";
            case "INR" : return "Индийская рупия";
            case "KZT" : return "Казахский тенге";
            case "CAD" : return "Канадский доллар";
            case "KGS" : return "Киргизский сом";
            case "CNY" : return "Китайский юань";
            case "MDL" : return "Молдавский лей";
            case "TMT" : return "Туркменский манат";
            case "NOK" : return "Норвежская крона";
            case "PLN" : return "Польский злотый";
            case "RON" : return "Румынский лей";
            case "XDR" : return "СДР";
            case "SGD" : return "Сингапурский доллар";
            case "TJS" : return "Таджикский сомони";
            case "TRY" : return "Турецкая лира";
            case "UZS" : return "Узбекский сум";
            case "UAH" : return "Украинская гривна";
            case "GBP" : return "Фунт Стерлингов";
            case "CZK" : return "Чешская крона";
            case "SEK" : return "Шведская крона";
            case "CHF" : return "Швейцарский франк";
            case "ZAR" : return "Южноафриканский рэнд";
            case "JPY" : return "Японская йена";
            case "RUB" : return "Российский рубль";
        }
        return null;
    }

    public void createDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Покормите кота!")
                .setCancelable(false)
                .setNegativeButton("ОК, иду на кухню",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static class SetCurrenciesDialog extends DialogFragment {

        public Dialog onCreateDialog (Bundle savedInstanceState) {
            Log.d("ui", "created new SetCurrenciesDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.equals)
                    .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }
}
