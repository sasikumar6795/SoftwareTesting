package com.sasicodes.softwareTesting.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        // When
        // Then

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
}