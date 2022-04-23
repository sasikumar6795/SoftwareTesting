package com.sasicodes.softwareTesting.stripe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasicodes.softwareTesting.customer.Customer;
import com.sasicodes.softwareTesting.customer.CustomerRegistrationRequest;
import com.sasicodes.softwareTesting.payment.Currency;
import com.sasicodes.softwareTesting.payment.Payment;
import com.sasicodes.softwareTesting.payment.PaymentRepository;
import com.sasicodes.softwareTesting.payment.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given a customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId,"sasi","00000");

        //Register
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        ResultActions customerResultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectToJson(customerRegistrationRequest)));
        // payment
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, customerId,new BigDecimal("10.00"), Currency.INR,"xox","fund");
        //payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        // when payment is sent
        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(ObjectToJson(paymentRequest))));
        // Then both customer registration and payment request are 200 status
        customerResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        //payment store in db
        // do not get payment repository instead create an endpoint to retrieve payments for customer

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p).isEqualToComparingFieldByField(payment);
                });
    }

    private String ObjectToJson(Object object) {
        try
        {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            fail("failed to convert object to string");
            return null;
        }
    }
}
