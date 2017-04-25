package com.xpendence.development.currencyconverter.operations;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Парсинг сайта ЦБ РФ для обновления БД
 * Created by promoscow on 21.04.17.
 */

public class Strategy {
    private Map<String, Currency> currencies = new HashMap<>();
    private final String URL_FORMAT = "http://www.cbr.ru/currency_base/daily.aspx?date_req=%s";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
    private static final String REFERRER = "none";

    protected Document getDocument() throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, getDate())).userAgent(USER_AGENT).referrer(REFERRER).get();
    }

    public static String getDate() {
//        return "11.11.2011";
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    public Map<String, Currency> getCurrencies() {
        Map<String, Currency> map = new HashMap<>();
        Document document = null;
        try {
            document = getDocument();
            Log.d("process", "document created");
            String e = document.getElementsByAttributeValue("class", "data").get(0).getElementsByTag("td").text();
//            System.out.println(e);
            StringTokenizer stringTokenizer = new StringTokenizer(e, " ");
            while (stringTokenizer.hasMoreTokens()) {
                String validToken = stringTokenizer.nextToken();
                if (validToken.length() == 3 && areDigits(validToken)) {
                    Currency currency = new Currency();
                    currency.setdCode(validToken);
                    currency.setCode(stringTokenizer.nextToken());
                    currency.setForAmount(Integer.parseInt(stringTokenizer.nextToken()));
                    String s = null;
                    while (true) {
                        if ((s = stringTokenizer.nextToken()).matches("^[0-9,]+$")) break;
                    }
                    currency.setRate(Double.parseDouble(s.replace(",", ".")));
                    currency.setDate(getDate());
                    map.put(currency.getCode(), currency);
                    Log.d("adding to array", currency.toString());
                }
            }
            Currency currency = new Currency("643", "RUB", 1, 1.0, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            map.put("RUB", currency);
            Log.d("adding to array", map.get("RUB").toString());
        } catch (IOException e) {
            Log.d("exception", "catched");
            e.printStackTrace();
        }
        return map;
    }

    private boolean areDigits(String validToken) {
        char[] ch = validToken.toCharArray();
        for (char c : ch) if (!Character.isDigit(c)) return false;
        return true;
    }
}
