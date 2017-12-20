package com.isa.pad.marketwarehouse.controller;

import com.isa.pad.marketwarehouse.model.Order;
import com.isa.pad.marketwarehouse.model.OrderLine;
import com.isa.pad.marketwarehouse.service.CustomerService;
import com.isa.pad.marketwarehouse.service.OrderLineService;
import com.isa.pad.marketwarehouse.service.OrderService;
import com.isa.pad.marketwarehouse.service.ProductService;
import com.isa.pad.marketwarehouse.util.JsonSerializer;
import com.isa.pad.marketwarehouse.util.JsonValidator;
import com.isa.pad.marketwarehouse.util.XmlSerializer;
import com.isa.pad.marketwarehouse.util.XmlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Faust on 12/20/2017.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @Autowired
    CustomerService customerService;

    @Autowired
    OrderLineService orderLineService;

    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public HttpEntity<List<Order>> getAllOrders() {
        List<Order> allOrders = orderService.findAll();
        if (allOrders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            allOrders.forEach(o -> o.add(linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders")));
            allOrders.forEach(o -> o.add(linkTo(methodOn(OrderController.class).getOrderById(o.getOrderId())).withSelfRel()));
            allOrders.forEach(o -> o.getCustomer().add(linkTo(methodOn(CustomerController.class).getCustomerById(o.getCustomer().getCustomerId())).withRel("customer")));
            allOrders.forEach(o -> {
                o.getOrderLineList().forEach(orderLine -> orderLine.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(orderLine.getOrderLineId())).withRel("orderline")));
                o.getOrderLineList().forEach(orderLine -> orderLine.getProduct().add(linkTo(methodOn(ProductController.class).getProductById(orderLine.getProduct().getProductId())).withRel("product")));
            });
            return new ResponseEntity<>(allOrders, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
    public HttpEntity<?> getOrderById(@PathVariable("id") String orderId) {
        Order byOrderId = orderService.findByOrderId(orderId);
        if (byOrderId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No order was found with id =" + orderId);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            byOrderId.add(linkTo(methodOn(OrderController.class).getOrderById(orderId)).withSelfRel());
            byOrderId.getCustomer().add(linkTo(methodOn(CustomerController.class).getCustomerById(byOrderId.getCustomer().getCustomerId())).withRel("customer"));
            byOrderId.getOrderLineList().forEach(orderLine -> orderLine.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(orderLine.getOrderLineId())).withRel("orderline")));
            byOrderId.getOrderLineList().forEach(orderLine -> orderLine.getProduct().add(linkTo(methodOn(ProductController.class).getProductById(orderLine.getProduct().getProductId())).withRel("product")));
            return new ResponseEntity<>(byOrderId, HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/order/", method = RequestMethod.GET, params = {"start", "end"})
    public ResponseEntity<?> getAllOrdersByLimit(@RequestParam("start") int start, @RequestParam("end") int end) {
        List<Order> allOrders = orderService.findAllByLimit(start, end);
        if (allOrders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            allOrders.forEach(o -> o.add(linkTo(methodOn(OrderController.class).getAllOrdersByLimit(start, end)).withRel("paging")));
            allOrders.forEach(o -> o.add(linkTo(methodOn(OrderController.class).getOrderById(o.getOrderId())).withSelfRel()));
            allOrders.forEach(o -> o.getCustomer().add(linkTo(methodOn(CustomerController.class).getCustomerById(o.getCustomer().getCustomerId())).withRel("customer")));
            allOrders.forEach(o -> {
                o.getOrderLineList().forEach(orderLine -> orderLine.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(orderLine.getOrderLineId())).withRel("orderline")));
                o.getOrderLineList().forEach(orderLine -> orderLine.getProduct().add(linkTo(methodOn(ProductController.class).getProductById(orderLine.getProduct().getProductId())).withRel("product")));
            });
            return new ResponseEntity<>(allOrders, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/order/", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestHeader(value = "Content-Type") String contentType, @RequestBody String body, UriComponentsBuilder uriComponentsBuilder) {
        if (contentType.contains("xml")) {
            XmlValidator xmlValidator = new XmlValidator("order_schema.xsd", Order.class);
            if (xmlValidator.validate(body)) {
                XmlSerializer xmlSerializer = new XmlSerializer();
                Order o = xmlSerializer.fromXml(body, Order.class);
                o.setCustomer(customerService.findByCustomerId(o.getCustomer().getCustomerId()));
                List<OrderLine> orderLineList = new ArrayList<>();
                o.getOrderLineList().forEach(orderLine -> orderLineList.add(orderLineService.findByOrderLineId(orderLine.getOrderLineId())));
                o.setOrderLineList(orderLineList);
                return createOrderFromSerialized(uriComponentsBuilder, o);
            } else {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("X-ErrorResponse", "XML Validation failed with : " + xmlValidator.getMessage());
                return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
            }
        } else if (contentType.contains("json")) {
            JsonValidator jsonValidator = new JsonValidator("order_schema.json");
            if (jsonValidator.validate(body)) {
                JsonSerializer jsonSerializer = new JsonSerializer();
                Order o = jsonSerializer.fromJson(body, Order.class);
                o.setCustomer(customerService.findByCustomerId(o.getCustomer().getCustomerId()));
                List<OrderLine> orderLineList = new ArrayList<>();
                o.getOrderLineList().forEach(orderLine -> orderLineList.add(orderLineService.findByOrderLineId(orderLine.getOrderLineId())));
                o.setOrderLineList(orderLineList);
                return createOrderFromSerialized(uriComponentsBuilder, o);
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

    private ResponseEntity<?> createOrderFromSerialized(UriComponentsBuilder uriComponentsBuilder, Order o) {
        if (orderService.orderExists(o)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to create order. Already exists.");
            return new ResponseEntity<>(httpHeaders, HttpStatus.CONFLICT);
        } else {
            Order order = orderService.saveOrder(o);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uriComponentsBuilder.path("/orders/order/{id}").buildAndExpand(order.getOrderId()).toUri());
            return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/order/{id}", method = RequestMethod.PUT, consumes = {"application/json", "application/xml", "application/yaml"})
    public ResponseEntity<?> updateOrder(@PathVariable("id") String id, @RequestBody Order o) {
        Order foundOrder = orderService.findByOrderId(id);
        if (foundOrder != null) {
            foundOrder.setOrderDate(o.getOrderDate());
            List<OrderLine> orderLineList = new ArrayList<>();
            foundOrder.getOrderLineList().forEach(orderLine -> orderLineList.add(orderLineService.findByOrderLineId(orderLine.getOrderLineId())));
            foundOrder.setOrderLineList(orderLineList);
            foundOrder.setCustomer(customerService.findByCustomerId(foundOrder.getCustomer().getCustomerId()));
            orderService.updateOrder(foundOrder);
            foundOrder.add(linkTo(methodOn(OrderController.class).getOrderById(foundOrder.getOrderId())).withSelfRel());
            foundOrder.getCustomer().add(linkTo(methodOn(CustomerController.class).getCustomerById(foundOrder.getCustomer().getCustomerId())).withRel("customer"));
            foundOrder.getOrderLineList().forEach(orderLine -> orderLine.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(orderLine.getOrderLineId())).withRel("orderline")));
            foundOrder.getOrderLineList().forEach(orderLine -> orderLine.getProduct().add(linkTo(methodOn(ProductController.class).getProductById(orderLine.getProduct().getProductId())).withRel("product")));
            return new ResponseEntity<>(foundOrder, HttpStatus.OK);
        } else {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to update. No such order with id = " + id);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/order/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteOrder(@PathVariable("id") String orderId) {
        orderService.deleteByOrderId(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/order/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllOrders() {
        orderService.deleteAllOrders();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
