package com.sasicodes.softwareTesting.customer;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

//    @Query(
//            value = "select id, name, phone_number " +
//                    "from customer where phone_number = :phone_number",
//            nativeQuery = true
//    )
//    Optional<Customer> selectCustomerByPhoneNumber(
//            @Param("phone_number") String phoneNumber);

    @Query("select c from Customer c where phoneNumber=?1")
    Optional<Customer>selectCustomerByPhoneNumber(String phoneNumber);

}