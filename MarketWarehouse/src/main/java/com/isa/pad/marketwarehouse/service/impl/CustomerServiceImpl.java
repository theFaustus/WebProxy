package com.isa.pad.marketwarehouse.service.impl;

import com.google.common.collect.Lists;
import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.repository.CustomerRepository;
import com.isa.pad.marketwarehouse.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer findByCustomerId(String customerId) {
        return customerRepository.findOne(customerId);
    }

    @Override
    public List<Customer> findByFirstNameAndLastName(String fname, String lname) {
        return customerRepository.findByFirstNameAndLastName(fname, lname);
    }

    @Override
    public List<Customer> findByAnyField(String q) {
        return customerRepository.findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContainingOrAddressIgnoreCaseContaining(q, q, q);
    }

    @Override
    public List<Customer> findByFirstNameStartsWith(String text) {
        return customerRepository.findByFirstNameStartingWith(text);
    }

    @Override
    public List<Customer> findAllByLimit(int startIndex, int endIndex) {
        Pageable pageable = new PageRequest(startIndex, endIndex);
        Page<Customer> all = customerRepository.findAll(pageable);
        return Lists.newArrayList(all);
    }

    @Override
    public Customer saveCustomer(Customer c) {
        return customerRepository.save(c);
    }

    @Override
    public void updateCustomer(Customer c) {
        customerRepository.save(c);
    }

    @Override
    public void deleteByCustomerId(String customerId) {
        customerRepository.delete(customerId);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public boolean customerExists(Customer c) {
        return customerRepository.exists(Example.of(c));
    }

    @Override
    public void deleteAllCustomers() {
        customerRepository.deleteAll();
    }
}
