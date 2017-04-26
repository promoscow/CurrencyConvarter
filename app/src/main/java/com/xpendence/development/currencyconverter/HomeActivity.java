package com.xpendence.development.currencyconverter;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.xpendence.development.currencyconverter.operations.Calculator;
import com.xpendence.development.currencyconverter.operations.Converter;
import com.xpendence.development.currencyconverter.operations.Currency;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

public class HomeActivity extends AppCompatActivity {
    private Map<String, Currency> currencies;
    public static String currencyFrom;
    public static String currencyTo;
    public static int amount;
    public static boolean isFirstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currencies = Converter.currencies;
        currencyFrom = currencies.get("USD").getCode();
        currencyTo = currencies.get("RUB").getCode();
        amount = 1;

//        setAllItems();
        setRate();

    }

//    public void setAllItems() {
//        for (String s : currencies.keySet()) Log.d("currency", s);
//
//        final String[] currenciesArray = new String[currencies.size()];
//        int i = 0;
//        for (Currency s : currencies.values())
//            currenciesArray[i++] = String.format("%s (%s)", s.getCode(), geCurrencyName(s));
//
//        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
//        ArrayAdapter<String> arrayAdapterFrom = new ArrayAdapter<String>(this,
//                R.layout.support_simple_spinner_dropdown_item, currenciesArray);
//        arrayAdapterFrom.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        spinnerFrom.setAdapter(arrayAdapterFrom);
//        int indexFrom = Arrays.asList(currenciesArray).indexOf("USD (Доллар США)");
//        spinnerFrom.setSelection(indexFrom);
//
//        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent,
//                                       View itemSelected, int selectedItemPosition, long selectedId) {
//
//                Log.d("position", String.valueOf(currenciesArray[selectedItemPosition]));
//                currencyFrom = currencies
//                        .get(new StringTokenizer(String.valueOf(currenciesArray[selectedItemPosition]), " ")
//                                .nextToken())
//                        .getCode();
//                Log.d("new currency", currencyFrom);
//                setRate();
//
////                Toast toast = Toast.makeText(getApplicationContext(),
////                        "Ваш выбор: " + currenciesArray[selectedItemPosition], Toast.LENGTH_SHORT);
////                toast.show();
//            }
//
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
//        ArrayAdapter<String> arrayAdapterTo = new ArrayAdapter<String>(this,
//                R.layout.support_simple_spinner_dropdown_item, currenciesArray);
//        arrayAdapterTo.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        spinnerTo.setAdapter(arrayAdapterTo);
//        int indexTo = Arrays.asList(currenciesArray).indexOf("RUB (Российский рубль)");
//        spinnerTo.setSelection(indexTo);
//
//        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent,
//                                       View itemSelected, int selectedItemPosition, long selectedId) {
//
//                currencyTo = currencies
//                        .get(new StringTokenizer(String.valueOf(currenciesArray[selectedItemPosition]), " ")
//                                .nextToken())
//                        .getCode();
//                setRate();
////                Toast toast = Toast.makeText(getApplicationContext(),
////                        "Ваш выбор: " + currenciesArray[selectedItemPosition], Toast.LENGTH_SHORT);
////                toast.show();
//            }
//
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        EditText editText = (EditText) findViewById(R.id.editTextAmount);
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s != null && s.length() != 0) {
//                    amount = Integer.parseInt(String.valueOf(s));
//                }
//                if (amount <= 0) amount = 1;
//                setRate();
//            }
//        });
//    }

    public void createDialog(View view) {
        for (String s : currencies.keySet()) Log.d("currency createDialog", s);
        final String[] currenciesArray = new String[currencies.size()];
        int i = 0;
        for (Currency s : currencies.values())
            currenciesArray[i++] = String.format("%s (%s)", s.getCode(), geCurrencyName(s));
        int indexFrom = Arrays.asList(currenciesArray).indexOf(String.format("%s (%s)",
                currencies.get(currencyFrom).getCode(),
                geCurrencyName(currencies.get(currencyFrom))));
        int indexTo = Arrays.asList(currenciesArray).indexOf(String.format("%s (%s)",
                currencies.get(currencyTo).getCode(),
                geCurrencyName(currencies.get(currencyTo))));

        LayoutInflater li = LayoutInflater.from(view.getContext());
        View promptsView = li.inflate(R.layout.dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        /**
         * Создание @SpinnerFrom
         */
        Spinner dialogSpinnerFrom = (Spinner) promptsView
                .findViewById(R.id.spinnerFrom);

        ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, currenciesArray) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setPadding(32, 32, 32, 32);
                return view;
            }
        };
        dialogSpinnerFrom.setAdapter(adapterFrom);
        dialogSpinnerFrom.setSelection(indexFrom);
        dialogSpinnerFrom.setOnItemSelectedListener(new OnSpinnerItemClickedFrom());

        /**
         * Создание @SpinnerTo
         */
        Spinner dialogSpinnerTo = (Spinner) promptsView
                .findViewById(R.id.spinnerTo);

        ArrayAdapter<String> adapterTo = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, currenciesArray) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setPadding(32, 32, 32, 32);
                return view;
            }
        };
        dialogSpinnerTo.setAdapter(adapterTo);
        dialogSpinnerTo.setSelection(indexTo);
        dialogSpinnerTo.setOnItemSelectedListener(new OnSpinnerItemClickedTo());

        /**
         * Создание @textEdit
         */
        final EditText editDisplayText = (EditText) promptsView.findViewById(R.id.editDisplayTextAmount);
        editDisplayText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String s = editDisplayText.getText().toString();
                    if (s.length() != 0) amount = Integer.parseInt(s);
                    if (amount <= 0) amount = 1;
                    alertDialog.cancel();
                    setRate();
                    return true;
                }
                return false;
            }
        });

        /**
         * Показ диалогового окна.
         */
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private class OnSpinnerItemClickedFrom implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Log.d("onItemSelected / Dialog", "enter");
            String[] currenciesArrayDisplay = new String[currencies.size()];
            int i = 0;
            for (Currency s : currencies.values())
                currenciesArrayDisplay[i++] = String.format("%s (%s)", s.getCode(), geCurrencyName(s));

            currencyFrom = currencies
                    .get(new StringTokenizer(String.valueOf(currenciesArrayDisplay[pos]), " ")
                            .nextToken())
                    .getCode();
            Log.d("currency onItemSelected", currencyFrom);
            setRate();

//            Toast.makeText(parent.getContext(), "Clicked : " +
//                    parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
        }
    }

    private class OnSpinnerItemClickedTo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Log.d("onItemSelected / Dialog", "enter");
            String[] currenciesArrayDisplay = new String[currencies.size()];
            int i = 0;
            for (Currency s : currencies.values())
                currenciesArrayDisplay[i++] = String.format("%s (%s)", s.getCode(), geCurrencyName(s));

            currencyTo = currencies
                    .get(new StringTokenizer(String.valueOf(currenciesArrayDisplay[pos]), " ")
                            .nextToken())
                    .getCode();
            Log.d("currency onItemSelected", currencyTo);
            setRate();
//            Toast.makeText(parent.getContext(), "Clicked : " +
//                    parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
        }
    }

    public void setRate() {
        double x = 0;
        try {
            x = Calculator.calculate(currencies, currencyFrom, currencyTo, amount);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView currencyFrom = (TextView) findViewById(R.id.textViewFrom);
        String result1 = String.valueOf(amount) + " " + HomeActivity.currencyFrom;
        currencyFrom.setText(result1);

        TextView currencyTo = (TextView) findViewById(R.id.textViewTo);
        String result2 = x + " " + HomeActivity.currencyTo;
        currencyTo.setText(result2);
    }

    private String geCurrencyName(Currency s) {
        switch (s.getCode()) {
            case "AUD":
                return "Австралийский доллар";
            case "AZN":
                return "Азербайджанский манат";
            case "AMD":
                return "Армянский драм";
            case "BYN":
                return "Белорусский рубль";
            case "BGN":
                return "Болгарский лев";
            case "BRL":
                return "Бразильский реал";
            case "HUF":
                return "Венгерский форинт";
            case "KRW":
                return "Южнокорейский вон";
            case "HKD":
                return "Гонгконгский доллар";
            case "DKK":
                return "Датская крона";
            case "USD":
                return "Доллар США";
            case "EUR":
                return "Евро";
            case "INR":
                return "Индийская рупия";
            case "KZT":
                return "Казахский тенге";
            case "CAD":
                return "Канадский доллар";
            case "KGS":
                return "Киргизский сом";
            case "CNY":
                return "Китайский юань";
            case "MDL":
                return "Молдавский лей";
            case "TMT":
                return "Туркменский манат";
            case "NOK":
                return "Норвежская крона";
            case "PLN":
                return "Польский злотый";
            case "RON":
                return "Румынский лей";
            case "XDR":
                return "СДР";
            case "SGD":
                return "Сингапурский доллар";
            case "TJS":
                return "Таджикский сомони";
            case "TRY":
                return "Турецкая лира";
            case "UZS":
                return "Узбекский сум";
            case "UAH":
                return "Украинская гривна";
            case "GBP":
                return "Фунт Стерлингов";
            case "CZK":
                return "Чешская крона";
            case "SEK":
                return "Шведская крона";
            case "CHF":
                return "Швейцарский франк";
            case "ZAR":
                return "Южноафриканский рэнд";
            case "JPY":
                return "Японская йена";
            case "RUB":
                return "Российский рубль";
        }
        return null;
    }
}
