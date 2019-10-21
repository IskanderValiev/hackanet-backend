package com.hackanet.models.enums;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/21/19
 */
public enum Currency {
    USD("$"), EUR("€"), GBP("£"), RUB("₽");

    private String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }
}
