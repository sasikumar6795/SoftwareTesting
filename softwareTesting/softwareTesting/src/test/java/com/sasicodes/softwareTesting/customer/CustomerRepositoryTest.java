package com.sasicodes.softwareTesting.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "5834";
        Customer customer = new Customer(id, "sasi", phoneNumber);
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumber() {
        // Given
        String phoneNumber = "5834";
        // When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        // Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "sasi", "5834");
        // When
        underTest.save(customer);
        // Then
        //get the customer back
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()==(id));
//                    assertThat(c.getName().equals("kumar"));
//                    assertThat(c.getPhoneNumber().compareTo("0000"));
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSaveWhenCustomerNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "5834");
        // When
        // underTest.save(customer);
        // Then
        //assertThat(underTest.findById(id)).isNotPresent();

        //When
        //Then
        assertThatThrownBy(()->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.sasicodes.softwareTesting.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);

    }
}