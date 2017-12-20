package com.isa.pad.marketwarehouse.controller;

import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.service.CustomerService;
import com.isa.pad.marketwarehouse.util.JsonSerializer;
import com.isa.pad.marketwarehouse.util.JsonValidator;
import com.isa.pad.marketwarehouse.util.XmlSerializer;
import com.isa.pad.marketwarehouse.util.XmlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    CustomerService customerService;

    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public HttpEntity<List<Customer>> getAllCustomers() {
        List<Customer> allCustomers = customerService.findAll();
        if (allCustomers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers")));
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerById(c.getCustomerId())).withSelfRel()));
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/customer/", method = RequestMethod.GET, params = {"fname", "lname"})
    public HttpEntity<?> getCustomerByFirstNameAndLastName(@RequestParam("fname") String fname, @RequestParam("lname") String lname) {
        List<Customer> allCustomers = customerService.findByFirstNameAndLastName(fname, lname);
        if (allCustomers.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No customers were found with credentials =" + fname + ", " + lname);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerByFirstNameAndLastName(fname, lname)).withRel("customers")));
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerById(c.getCustomerId())).withSelfRel()));
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
    public HttpEntity<?> getCustomerById(@PathVariable("id") String customerId) {
        Customer byCustomerId = customerService.findByCustomerId(customerId);
        if (byCustomerId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No customer was found with id =" + customerId);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            byCustomerId.add(linkTo(methodOn(CustomerController.class).getCustomerById(customerId)).withSelfRel());
            return new ResponseEntity<>(byCustomerId, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/customer/", method = RequestMethod.GET, params = "q")
    public ResponseEntity<?> getCustomerByAnyField(@RequestParam("q") String q) {
        List<Customer> allCustomers = customerService.findByAnyField(q);
        if (allCustomers.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No such customers with requested query = " + q);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerByAnyField(q)).withRel("query")));
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerById(c.getCustomerId())).withSelfRel()));
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/customer/", method = RequestMethod.GET, params = "fname_starts_with")
    public ResponseEntity<?> getCustomerByFirstNameStartsWith(@RequestParam("fname_starts_with") String text) {
        List<Customer> allCustomers = customerService.findByFirstNameStartsWith(text);
        if (allCustomers.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No such customers which start with = " + text);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerByFirstNameStartsWith(text)).withRel("startsWith")));
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerById(c.getCustomerId())).withSelfRel()));
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/customer/", method = RequestMethod.GET, params = {"start", "end"})
    public ResponseEntity<?> getAllCustomersByLimit(@RequestParam("start") int start, @RequestParam("end") int end) {
        List<Customer> allCustomers = customerService.findAllByLimit(start, end);
        if (allCustomers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getAllCustomersByLimit(start, end)).withRel("paging")));
            allCustomers.forEach(c -> c.add(linkTo(methodOn(CustomerController.class).getCustomerById(c.getCustomerId())).withSelfRel()));
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/customer/", method = RequestMethod.POST)
    public ResponseEntity<?> createCustomer(@RequestHeader(value = "Content-Type") String contentType, @RequestBody String body, UriComponentsBuilder uriComponentsBuilder) {
        if (contentType.contains("xml")) {
            XmlValidator xmlValidator = new XmlValidator("customer_schema.xsd", Customer.class);
            if (xmlValidator.validate(body)) {
                XmlSerializer xmlSerializer = new XmlSerializer();
                Customer c = xmlSerializer.fromXml(body, Customer.class);
                return createCustomerFromSerialized(uriComponentsBuilder, c);
            } else {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("X-ErrorResponse", "XML Validation failed with : " + xmlValidator.getMessage());
                return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
            }
        } else if (contentType.contains("json")) {
            JsonValidator jsonValidator = new JsonValidator("customer_schema.json");
            if (jsonValidator.validate(body)) {
                JsonSerializer jsonSerializer = new JsonSerializer();
                Customer c = jsonSerializer.fromJson(body, Customer.class);
                return createCustomerFromSerialized(uriComponentsBuilder, c);
            } else {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("X-ErrorResponse", "JSON Validation failed with : " + jsonValidator.getMessage());
                return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-ErrorResponse", "Something happened.");
        return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> createCustomerFromSerialized(UriComponentsBuilder uriComponentsBuilder, Customer c) {
        if (customerService.customerExists(c)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to create customer. Already exists.");
            return new ResponseEntity<>(httpHeaders, HttpStatus.CONFLICT);
        } else {
            Customer customer = customerService.saveCustomer(c);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uriComponentsBuilder.path("/customers/customer/{id}").buildAndExpand(customer.getCustomerId()).toUri());
            return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.PUT, consumes = {"application/json", "application/xml", "application/yaml"})
    public ResponseEntity<?> updateCustomer(@PathVariable("id") String id, @RequestBody Customer c) {
        Customer foundCustomer = customerService.findByCustomerId(id);
        if (foundCustomer != null) {
            foundCustomer.setFirstName(StringUtils.isEmpty(c.getFirstName()) ? foundCustomer.getFirstName() : c.getFirstName());
            foundCustomer.setLastName(StringUtils.isEmpty(c.getLastName()) ? foundCustomer.getLastName() : c.getLastName());
            foundCustomer.setAddress(StringUtils.isEmpty(c.getAddress()) ? foundCustomer.getAddress() : c.getAddress());
            customerService.updateCustomer(foundCustomer);
            foundCustomer.add(linkTo(methodOn(CustomerController.class).getCustomerById(foundCustomer.getCustomerId())).withSelfRel());
            return new ResponseEntity<>(foundCustomer, HttpStatus.OK);
        } else {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to update. No such customer with id = " + id);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") String customerId) {
        customerService.deleteByCustomerId(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/customer/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllCustomers() {
        customerService.deleteAllCustomers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
