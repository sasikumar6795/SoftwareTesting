package com.sasicodes.softwareTesting.payment;

import com.sasicodes.softwareTesting.customer.Customer;
import com.sasicodes.softwareTesting.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(customerRepository,paymentRepository,cardPaymentCharger);
    }


    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Currency currency = Currency.INR;

        PaymentRequest paymentRequest = new PaymentRequest( new Payment(
                null,
                null,
                new BigDecimal("100.00"),
                currency,
                "hdfc",
                "donation"
        ));

        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(customerId,paymentRequest);
        // Then

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);

    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Currency currency = Currency.INR;

        PaymentRequest paymentRequest = new PaymentRequest( new Payment(
                null,
                null,
                new BigDecimal("100.00"),
                currency,
                "hdfc",
                "donation"
        ));

        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));


        // Then

        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("card not debited for customer [%s]",customerId));

        //finally
        then(paymentRepository).should(never()).save(any(Payment.class));

    }

    @Test
    void itShouldThrowIsCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Currency currency = Currency.EUR;

        PaymentRequest paymentRequest = new PaymentRequest( new Payment(
                null,
                null,
                new BigDecimal("100.00"),
                currency,
                "hdfc",
                "donation"
        ));

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency [%s] is not supported",currency));

        //.. no interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoMoreInteractions();

        //finally
        //.. no interaction with payment repository
        then(paymentRepository).shouldHaveNoMoreInteractions();
    }


    @Test
    void itShouldThrowWhenCustomerIsNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        Currency currency = Currency.INR;

        PaymentRequest paymentRequest = new PaymentRequest( new Payment(
                null,
                null,
                new BigDecimal("100.00"),
                currency,
                "hdfc",
                "donation"
        ));

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Customer [%s] not found",customerId));

        //.. no interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoMoreInteractions();

        //finally
        //.. no interaction with payment repository
        then(paymentRepository).shouldHaveNoMoreInteractions();

    }
}