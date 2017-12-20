package com.isa.pad.marketwarehouse.repository;

import com.isa.pad.marketwarehouse.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface CustomerRepository extends MongoRepository<Customer, String>{
    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
    List<Customer> findByFirstNameStartingWith(String text);
    List<Customer> findByFirstName(String text);

}
