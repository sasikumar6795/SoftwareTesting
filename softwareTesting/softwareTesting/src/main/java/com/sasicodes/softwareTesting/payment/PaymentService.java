package com.sasicodes.softwareTesting.payment;

import com.sasicodes.softwareTesting.customer.Customer;
import com.sasicodes.softwareTesting.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD,Currency.INR,Currency.GBP);

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }
// sasi implementaion
//    void chargeCard(UUID customerId, PaymentRequest paymentRequest)
//    {
//        //1. Does customer exists if not throw
//        Optional<Customer> customerOptional = customerRepository.findById(customerId);
//        List<Currency> currencyList = List.of(Currency.values());
//
//        if(customerOptional.isPresent())
//        {
//            Customer customer = customerOptional.get();
//            //2. Do we support the currency if not throw
//            Currency requestCurrency = paymentRequest.getPayment().getCurrency();
//            if(currencyList.contains(requestCurrency))
//            {
//                //3.charge card
//                CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard("hdfc", BigDecimal.valueOf(100.00), Currency.INR, "Donation");
//
//                if(cardPaymentCharge.getCardDebited()){
//
//                }
//                throw new IllegalStateException("Card is not debited");
//            }
//            throw new IllegalStateException(String.format("Currency [%s] is not supported",requestCurrency));
//        }
//        throw new IllegalStateException("Customer not found");
//
//    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest){
        //1. Does customer exists if not throw
        boolean isCustomerFound = customerRepository.findById(customerId).isPresent();
        if(!isCustomerFound){
            throw new IllegalStateException(String.format("Customer [%s] not found",customerId));
        }
        //2. Do we support the currency if not throw
        Currency requestedCurrency = paymentRequest.getPayment().getCurrency();
        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream().anyMatch(c -> c.equals(requestedCurrency));
        if(!isCurrencySupported)
        {
            throw new IllegalStateException(String.format("Currency [%s] is not supported",requestedCurrency));
        }

        //3. chargeCard
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        //4. if not debited throw
        if(!cardPaymentCharge.getCardDebited())
        {
            throw new IllegalStateException(String.format("card not debited for customer [%s]",customerId));
        }

        //5. Insert payment
        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());
    }

}
