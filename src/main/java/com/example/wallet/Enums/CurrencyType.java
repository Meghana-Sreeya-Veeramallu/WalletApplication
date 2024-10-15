package com.example.wallet.Enums;

public enum CurrencyType {
    USD(84.0),
    EUR(91.0),
    GBP(109.0),
    JPY(0.55),
    INR(1.0);

    private final double conversionFactor;

    CurrencyType(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double toBaseCurrency(double amount) {
        return amount * conversionFactor;
    }

    public double fromBaseCurrency(double amount) {
        return amount / conversionFactor;
    }
}
