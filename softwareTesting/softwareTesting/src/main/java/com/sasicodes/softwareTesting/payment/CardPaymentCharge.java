package com.sasicodes.softwareTesting.payment;

public class CardPaymentCharge {

    private final Boolean isCardDebited;

    public CardPaymentCharge(Boolean isCardDebited) {
        this.isCardDebited = isCardDebited;
    }

    public Boolean getCardDebited() {
        return isCardDebited;
    }

    @Override
    public String toString() {
        return "CardPaymentCharge{" +
                "isCardDebited=" + isCardDebited +
                '}';
    }
}
