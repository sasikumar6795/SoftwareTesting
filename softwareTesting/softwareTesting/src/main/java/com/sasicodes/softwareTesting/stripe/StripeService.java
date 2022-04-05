package com.sasicodes.softwareTesting.stripe;

import com.sasicodes.softwareTesting.payment.CardPaymentCharge;
import com.sasicodes.softwareTesting.payment.CardPaymentCharger;
import com.sasicodes.softwareTesting.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService implements CardPaymentCharger {

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_51Kl1zySCsBe27pcuMpQOCUM0CMUpqnPSwHTot0BDKJGuiMRxku6eOMSr3e8EcXksQOtFOEcPfhq7UyoCgDKveMKA00AzJHbfxK")
            .build();

    private final StripeApi stripeApi;

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardPaymentCharge chargeCard(String cardSource,
                                        BigDecimal amount,
                                        Currency currency,
                                        String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("source", cardSource);
        params.put("currency", currency);
        params.put("amount", amount);
        params.put("description", description);

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            return new CardPaymentCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalStateException(String.format("charge not debited for cardSource [%s]",cardSource));
        }
    }
}
