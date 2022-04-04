package com.sasicodes.softwareTesting.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        // Given
        Payment payment = new Payment(1L, UUID.randomUUID(),
                new BigDecimal("10.00"),Currency.INR,"iciciCard","donation");
        // When
        underTest.save(payment);
        // Then
        Optional<Payment> foundPayment = underTest.findByPaymentId(1L);
        assertThat(foundPayment).isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));
    }
}