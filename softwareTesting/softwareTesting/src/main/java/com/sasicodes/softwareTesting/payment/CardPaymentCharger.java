package com.sasicodes.softwareTesting.payment;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(
            String cardSource,
            BigDecimal amount,
            Currency currency,
            String description
    );
}
