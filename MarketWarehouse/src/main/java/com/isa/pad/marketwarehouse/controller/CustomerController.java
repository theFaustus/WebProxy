package com.isa.pad.marketwarehouse.controller;

import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Faust on 12/20/2017.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public HttpEntity<List<Customer>> getAllCustomers() {
        List<Customer> allCustomers = customerRepository.findAll();
        allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getAllCustomers()).withSelfRel()));
        if (allCustomers.isEmpty())
            return new ResponseEntity<List<Customer>>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<List<Customer>>(allCustomers, HttpStatus.OK);
    }
}
