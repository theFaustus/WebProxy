package com.isa.pad.marketwarehouse.service;

import com.isa.pad.marketwarehouse.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Created by Faust on 12/20/2017.
 */
public interface CustomerService {
    Customer findByCustomerId(String customerId);

    List<Customer> findByFirstNameAndLastName(String fname, String lname);

    List<Customer> findByAnyField(String q);

    List<Customer> findByFirstNameStartsWith(String text);

    List<Customer> findAllByLimit(int startIndex, int endIndex);

    Customer saveCustomer(Customer c);

    void updateCustomer(Customer c);

    void deleteByCustomerId(String customerId);

    List<Customer> findAll();

    boolean customerExists(Customer c);

    void deleteAllCustomers();
}
