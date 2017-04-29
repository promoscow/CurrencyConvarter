package com.xpendence.development.currencyconverter.operations;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Парсинг сайта ЦБ РФ для обновления БД
 * Created by promoscow on 21.04.17.
 */

public class Strategy {
    private final String URL_FORMAT = "http://www.cbr.ru/currency_base/daily.aspx?date_req=%s";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
    private static final String REFERRER = "none";

    private final String XML_URL_FORMAT = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=%s";

    protected Document getDocument() throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, getDate())).userAgent(USER_AGENT).referrer(REFERRER).get();
    }

    public static String getDate() {
//        return "11.11.2011";
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    public Map<String, Currency> getCurrencies() {
//        Map<String, Currency> xmlMap = getXMLCurrencies();
        Map<String, Currency> map = new HashMap<>();
        Document document = null;
        try {
            document = getDocument();
            String e = document.getElementsByAttributeValue("class", "data").get(0).getElementsByTag("td").text();
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

    public Map<String, Currency> getXMLCurrencies() {
        Log.d("getXMLDocument", "enter");
        Map<String, Currency> map = new HashMap<>();

        Document document = null;
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            String date = getDate();
            document = Jsoup.connect(String.format(XML_URL_FORMAT, date))
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .get();
            builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(document.html())));
            doc.getDocumentElement().normalize();
            Log.d("Корневой элемент", doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Valute");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Currency currency = getXMLCurrency(nodeList.item(i), date);
                map.put(currency.getCode(), currency);
                Log.d("XMLcurrency", currency.toString());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Currency getXMLCurrency(Node item, String date) {
        Currency currency = new Currency();
        if (item.getNodeType() == item.ELEMENT_NODE) {
            Element element = (Element) item;
            currency.setdCode(getTagValue("NumCode", element));
            currency.setCode(getTagValue("CharCode", element));
            currency.setForAmount(Integer.parseInt(getTagValue("Nominal", element)));
            currency.setRate(Double.parseDouble(getTagValue("Value", element).replace(",", ".")));
            currency.setDate(getTagValue("Value", element));
        }
        return currency;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        String s = node.getNodeValue();
        Log.d("VALUE", s);
        return s;
    }
}
