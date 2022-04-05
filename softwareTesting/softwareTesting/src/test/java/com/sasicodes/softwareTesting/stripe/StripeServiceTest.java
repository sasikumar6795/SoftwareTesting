package com.sasicodes.softwareTesting.stripe;

import com.sasicodes.softwareTesting.payment.CardPaymentCharge;
import com.sasicodes.softwareTesting.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    @Mock
    private StripeApi stripeApi;

    private StripeService underTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest =  new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCardSuccessfully() throws StripeException {
        // Given
        String source="x0x0x";
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.INR;
        String description="Charing card test";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(),any())).willReturn(charge);
        // When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(source, amount, currency, description);

        // Then
        ArgumentCaptor<Map<String, Object>> mapParams= forClass(Map.class);
        ArgumentCaptor<RequestOptions> requestOptions = forClass(RequestOptions.class);

        then(stripeApi).should().create(mapParams.capture(),requestOptions.capture());

        Map<String, Object> mapValues = mapParams.getValue();

        assertThat(mapValues.keySet()).hasSize(4);

        assertThat(mapValues.get("amount")).isEqualTo(amount);
        assertThat(mapValues.get("source")).isEqualTo(source);
        assertThat(mapValues.get("currency")).isEqualTo(currency);
        assertThat(mapValues.get("description")).isEqualTo(description);

        assertThat(requestOptions.getValue()).isNotNull();

        assertThat(cardPaymentCharge.getCardDebited()).isTrue();


    }
}