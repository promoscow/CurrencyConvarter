package com.xpendence.development.currencyconverter.operations;

import java.io.Serializable;

/**
 * Класс валюты.
 * Created by promoscow on 21.04.17.
 */

public class Currency implements Serializable {
    private String dCode;
    private String code;
    private int forAmount;
    private double rate;
    private String date;

    public Currency(String dCode, String code, int forAmount, double rate, String date) {
        this.dCode = dCode;
        this.code = code;
        this.forAmount = forAmount;
        this.rate = rate;
        this.date = date;
    }

    public Currency() {
    }

    public String getdCode() {
        return dCode;
    }

    public void setdCode(String dCode) {
        this.dCode = dCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getForAmount() {
        return forAmount;
    }

    public void setForAmount(int forAmount) {
        this.forAmount = forAmount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "dCode='" + dCode + '\'' +
                ", code='" + code + '\'' +
                ", forAmount='" + forAmount + '\'' +
                ", rate='" + rate + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        return forAmount == currency.forAmount &&
                Double.compare(currency.rate, rate) == 0 &&
                (dCode != null ? dCode.equals(currency.dCode) : currency.dCode == null &&
                        (code != null ? code.equals(currency.code) : currency.code == null &&
                                (date != null ? date.equals(currency.date) : currency.date == null)));

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = dCode != null ? dCode.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + forAmount;
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
