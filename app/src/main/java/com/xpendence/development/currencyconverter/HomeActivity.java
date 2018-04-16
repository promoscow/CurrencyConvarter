package com.xpendence.development.currencyconverter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xpendence.development.currencyconverter.data.CurrenciesDBHelper;
import com.xpendence.development.currencyconverter.operations.Calculator;
import com.xpendence.development.currencyconverter.operations.Converter;
import com.xpendence.development.currencyconverter.operations.Currency;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Map<String, Currency> currencies;
    public static String currencyFrom;
    public static String currencyTo;
    public static double amount;

    private Spinner dialogSpinnerFrom;
    private Spinner dialogSpinnerTo;

    int indexFrom;
    int indexTo;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    String[] currenciesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        while (true) {
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.textView3);
                        textView.setText(getString(R.string.swipe_to_update)
                                + currencies.get("USD").getDate().replace("/", ".")
                                + ")");
                    }
                }, 100);
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        currencies = Converter.getCurrencies();
        for (Map.Entry<String, Currency> entry : currencies.entrySet()) {
            String s = entry.getKey() + " / " + entry.getValue();
            Log.d("HomeActivityCur", s);
        }
        currencyFrom = currencies.get("USD").getCode();
        currencyTo = currencies.get("RUB").getCode();
        amount = 1;

        currenciesArray = new String[currencies.size()];
        int i = 0;
        for (Currency s : currencies.values())
            currenciesArray[i++] = String.format("%s (%s)", s.getCode(), geCurrencyName(s));

        setRate();
        this.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            View focusView = this.getCurrentFocus();
            if (focusView != null) {
                focusView.clearFocus();
                ((InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(focusView.getWindowToken(), HIDE_NOT_ALWAYS);
            }
        } catch (NullPointerException e) {
            String message = e.getMessage();
            Log.v("KeyBrdUtils", message == null ? "" : message);
        }
    }

    public void createDialog(View view) {
        for (String s : currencies.keySet()) Log.d("currency createDialog", s);

        indexFrom = Arrays.asList(currenciesArray).indexOf(String.format("%s (%s)",
                currencies.get(currencyFrom).getCode(),
                geCurrencyName(currencies.get(currencyFrom))));
        indexTo = Arrays.asList(currenciesArray).indexOf(String.format("%s (%s)",
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
        dialogSpinnerFrom = promptsView.findViewById(R.id.spinnerFrom);

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
        dialogSpinnerTo = promptsView.findViewById(R.id.spinnerTo);

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
        final EditText editDisplayText = promptsView.findViewById(R.id.editDisplayTextAmount);
        editDisplayText.setText(String.valueOf(getAmountFromDouble(amount)));
        editDisplayText.setSelection(editDisplayText.getText().length());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        editDisplayText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("keyCode", String.valueOf(keyCode));
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String s = editDisplayText.getText().toString();
                    Log.d("enter numbers", s);
                    if (s.length() != 0) amount = Double.parseDouble(s);
                    if (amount <= 0) amount = 1;
                    alertDialog.cancel();
                    setRate();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(HIDE_NOT_ALWAYS, 0);
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

    private CharSequence getAmountFromDouble(double amount) {
        int digit = (int) amount;
        if (digit == amount) return String.valueOf(digit);
        else return String.valueOf(amount);
    }

    public void changeCurrencies(View view) {
        int temp = indexFrom;
        indexFrom = indexTo;
        indexTo = temp;
        dialogSpinnerFrom.setSelection(indexFrom);
        dialogSpinnerTo.setSelection(indexTo);
    }

    public static boolean isReallyOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onRefresh() {
        final TextView textView = findViewById(R.id.textView3);
        textView.setText("");

        if (!isReallyOnline()) {
            Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            textView.setText(getString(R.string.swipe_to_update)
                    + currencies.get("USD").getDate().replace("/", ".")
                    + ")");
            return;
        }

        CurrenciesDBHelper dbHelper = new CurrenciesDBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Converter converter = new Converter(database);
        try {
            Log.d("onRefresh invoke", converter.toString());
            converter.prepareDB();
            this.currencies = Converter.currencies;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(getBaseContext(), getString(R.string.updated) + " " + currencies.size(), Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
        textView.setText(getString(R.string.swipe_to_update)
                + currencies.get("USD").getDate().replace("/", ".")
                + ")");
    }

    public void showCredits(View view) {
        LayoutInflater li = LayoutInflater.from(view.getContext());
        View promptsView = li.inflate(R.layout.credits_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void voteForApplication(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.xpendence.development.currencyconvarter"));
        startActivity(intent);
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
            indexFrom = Arrays.asList(currenciesArray).indexOf(String.format("%s (%s)",
                    currencies.get(currencyFrom).getCode(),
                    geCurrencyName(currencies.get(currencyFrom))));
            Log.d("currency onItemSelected", currencyFrom);
            setRate();
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
            indexTo = Arrays.asList(currenciesArray).indexOf(String.format("%s (%s)",
                    currencies.get(currencyTo).getCode(),
                    geCurrencyName(currencies.get(currencyTo))));
            Log.d("currency onItemSelected", currencyTo);
            setRate();
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

        NumberFormat numberFormat = new DecimalFormat("0.00");
        numberFormat.setRoundingMode(RoundingMode.DOWN);

        TextView currencyFrom = findViewById(R.id.textViewFrom);

        String am = ((amount < 1000) && ((amount - (int) amount) == 0.0))
                ? String.valueOf((int) amount) : (amount < 1000)
                ? numberFormat.format(amount) : (amount < 100000)
                ? NumberFormat.getInstance(Locale.US).format((int) amount).replace(",", " ") : (amount < 1000000)
                ? String.valueOf((int) amount / 1000) + "k" : (amount < 100000000)
                ? String.valueOf((int) amount / 1000000) + "M" : "100M";
        String result1 = (am + " " + HomeActivity.currencyFrom).replace(",", ".");
        currencyFrom.setText(result1);

        TextView currencyTo = findViewById(R.id.textViewTo);
        String res = ((x < 1000) && ((x - (int) x) ==0.0))
                ? String.valueOf((int) x) : (x < 1000)
                ? String.valueOf(x) : (x < 100000)
                ? NumberFormat.getInstance(Locale.US).format((int) x).replace(",", " ") : (x < 1000000)
                ? String.valueOf(numberFormat.format(x / 1000)) + "k" : (x < 100000000)
                ? String.valueOf(numberFormat.format(x / 1000000)) + "M" : "> 100M";
        String result2 = res + " " + HomeActivity.currencyTo;
        currencyTo.setText(result2);
    }

    private String geCurrencyName(Currency s) {
        switch (s.getCode()) {
            case "AUD":
                return getString(R.string.aud);
            case "AZN":
                return getString(R.string.azn);
            case "AMD":
                return getString(R.string.amd);
            case "BYN":
                return getString(R.string.byn);
            case "BGN":
                return getString(R.string.bgn);
            case "BRL":
                return getString(R.string.brl);
            case "HUF":
                return getString(R.string.huf);
            case "KRW":
                return getString(R.string.krw);
            case "HKD":
                return getString(R.string.hkd);
            case "DKK":
                return getString(R.string.dkk);
            case "USD":
                return getString(R.string.usd);
            case "EUR":
                return getString(R.string.eur);
            case "INR":
                return getString(R.string.inr);
            case "KZT":
                return getString(R.string.kzt);
            case "CAD":
                return getString(R.string.cad);
            case "KGS":
                return getString(R.string.kgs);
            case "CNY":
                return getString(R.string.cny);
            case "MDL":
                return getString(R.string.mdl);
            case "TMT":
                return getString(R.string.tmt);
            case "NOK":
                return getString(R.string.nok);
            case "PLN":
                return getString(R.string.pln);
            case "RON":
                return getString(R.string.ron);
            case "XDR":
                return getString(R.string.xdr);
            case "SGD":
                return getString(R.string.sgd);
            case "TJS":
                return getString(R.string.tjs);
            case "TRY":
                return getString(R.string.trl);
            case "UZS":
                return getString(R.string.uzs);
            case "UAH":
                return getString(R.string.uah);
            case "GBP":
                return getString(R.string.gbp);
            case "CZK":
                return getString(R.string.czk);
            case "SEK":
                return getString(R.string.sek);
            case "CHF":
                return getString(R.string.chf);
            case "ZAR":
                return getString(R.string.zar);
            case "JPY":
                return getString(R.string.jpy);
            case "RUB":
                return getString(R.string.rub);
        }
        return null;
    }
}
