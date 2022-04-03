package com.sasicodes.softwareTesting.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    private CustomerRepository customerRepository= mock(CustomerRepository.class);
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
    private CustomerRegistrationService underTest;
    @BeforeEach
    void setUp() {
        underTest= new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldsaveNewCustomer(){
        // Given
        String phoneNumber = "00001";
        Customer customer = new Customer(java.util.UUID.randomUUID(), "sasi", phoneNumber);
        //.. a request
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // .. no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(customerRegistrationRequest);
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();
        assertThat(value).isEqualTo(customer);
    }

    @Test
    void itShouldsaveNewCustomerWhenIdIsNull(){
        // Given
        String phoneNumber = "00001";
        Customer customer = new Customer(null, "sasi", phoneNumber);
        //.. a request
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // .. no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(customerRegistrationRequest);
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();
        assertThat(value).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(value.getId()).isNotNull();
    }

    @Test
    void itShouldNoSaveCustomerWhenCustomerExists() {
        // Given
        String phoneNumber = "00001";
        java.util.UUID id = java.util.UUID.randomUUID();
        Customer customer = new Customer(id, "sasi", phoneNumber);
        //.. a request
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // .. and existing customer returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        // When
        underTest.registerNewCustomer(customerRegistrationRequest);
        // Then
        then(customerRepository).should(never()).save(customer);
        // then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        // then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        // Given
        String phoneNumber = "00001";
        java.util.UUID id = java.util.UUID.randomUUID();
        Customer customer = new Customer(id, "sasi", phoneNumber);
        Customer customerTwo = new Customer(id, "kumar", phoneNumber);
        //.. a request
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        // .. and existing customer returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customerTwo));
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));
        //finally
        then(customerRepository).should(never()).save(any(Customer.class));

    }
}