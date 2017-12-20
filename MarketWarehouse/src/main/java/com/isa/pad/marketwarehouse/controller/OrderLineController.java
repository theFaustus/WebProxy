package com.isa.pad.marketwarehouse.controller;

import com.isa.pad.marketwarehouse.model.OrderLine;
import com.isa.pad.marketwarehouse.service.OrderLineService;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Faust on 12/20/2017.
 */
@RestController
@RequestMapping("/orderlines")
public class OrderLineController {

    @Autowired
    OrderLineService orderLineService;

    @Autowired
    ProductService productService;

    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public HttpEntity<List<OrderLine>> getAllOrderLines() {
        List<OrderLine> allOrderLines = orderLineService.findAll();
        if (allOrderLines.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            allOrderLines.forEach(o -> o.add(linkTo(methodOn(OrderLineController.class).getAllOrderLines()).withRel("orderlines")));
            allOrderLines.forEach(o -> o.getProduct().add(linkTo(methodOn(ProductController.class).getProductById(o.getProduct().getProductId())).withRel("product")));
            allOrderLines.forEach(o -> o.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(o.getOrderLineId())).withSelfRel()));
            return new ResponseEntity<>(allOrderLines, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/orderline/{id}", method = RequestMethod.GET)
    public HttpEntity<?> getOrderLineById(@PathVariable("id") String orderLineId) {
        OrderLine byOrderLineId = orderLineService.findByOrderLineId(orderLineId);
        if (byOrderLineId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No orderline was found with id =" + orderLineId);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            byOrderLineId.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(orderLineId)).withSelfRel());
            byOrderLineId.add(linkTo(methodOn(ProductController.class).getProductById(byOrderLineId.getProduct().getProductId())).withRel("product"));
            return new ResponseEntity<>(byOrderLineId, HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/orderline/", method = RequestMethod.GET, params = {"start", "end"})
    public ResponseEntity<?> getAllOrderLinesByLimit(@RequestParam("start") int start, @RequestParam("end") int end) {
        List<OrderLine> allOrderLines = orderLineService.findAllByLimit(start, end);
        if (allOrderLines.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            allOrderLines.forEach(o -> o.add(linkTo(methodOn(OrderLineController.class).getAllOrderLinesByLimit(start, end)).withRel("paging")));
            allOrderLines.forEach(o -> o.getProduct().add(linkTo(methodOn(ProductController.class).getProductById(o.getProduct().getProductId())).withRel("product")));
            allOrderLines.forEach(o -> o.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(o.getOrderLineId())).withSelfRel()));
            return new ResponseEntity<>(allOrderLines, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/orderline/", method = RequestMethod.POST)
    public ResponseEntity<?> createOrderLine(@RequestHeader(value = "Content-Type") String contentType, @RequestBody String body, UriComponentsBuilder uriComponentsBuilder) {
        if (contentType.contains("xml")) {
            XmlValidator xmlValidator = new XmlValidator("orderline_schema.xsd", OrderLine.class);
            if (xmlValidator.validate(body)) {
                XmlSerializer xmlSerializer = new XmlSerializer();
                OrderLine o = xmlSerializer.fromXml(body, OrderLine.class);
                o.setProduct(productService.findByProductId(o.getProduct().getProductId()));
                return createOrderLineFromSerialized(uriComponentsBuilder, o);
            } else {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("X-ErrorResponse", "XML Validation failed with : " + xmlValidator.getMessage());
                return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
            }
        } else if (contentType.contains("json")) {
            JsonValidator jsonValidator = new JsonValidator("orderline_schema.json");
            if (jsonValidator.validate(body)) {
                JsonSerializer jsonSerializer = new JsonSerializer();
                OrderLine o = jsonSerializer.fromJson(body, OrderLine.class);
                o.setProduct(productService.findByProductId(o.getProduct().getProductId()));
                return createOrderLineFromSerialized(uriComponentsBuilder, o);
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

    private ResponseEntity<?> createOrderLineFromSerialized(UriComponentsBuilder uriComponentsBuilder, OrderLine o) {
        if (orderLineService.orderLineExists(o)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to create orderline. Already exists.");
            return new ResponseEntity<>(httpHeaders, HttpStatus.CONFLICT);
        } else {
            OrderLine orderline = orderLineService.saveOrderLine(o);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uriComponentsBuilder.path("/orderlines/orderline/{id}").buildAndExpand(orderline.getOrderLineId()).toUri());
            return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/orderline/{id}", method = RequestMethod.PUT, consumes = {"application/json", "application/xml", "application/yaml"})
    public ResponseEntity<?> updateOrderLine(@PathVariable("id") String id, @RequestBody OrderLine o) {
        OrderLine foundOrderLine = orderLineService.findByOrderLineId(id);
        if (foundOrderLine != null) {
            foundOrderLine.setProduct(StringUtils.isEmpty(o.getProduct()) ? foundOrderLine.getProduct() : o.getProduct());
            foundOrderLine.setAmount(StringUtils.isEmpty(o.getAmount()) ? foundOrderLine.getAmount() : o.getAmount());
            orderLineService.updateOrderLine(foundOrderLine);
            foundOrderLine.add(linkTo(methodOn(OrderLineController.class).getOrderLineById(foundOrderLine.getOrderLineId())).withSelfRel());
            foundOrderLine.add(linkTo(methodOn(ProductController.class).getProductById(foundOrderLine.getProduct().getProductId())).withRel("product"));
            return new ResponseEntity<>(foundOrderLine, HttpStatus.OK);
        } else {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to update. No such orderline with id = " + id);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/orderline/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteOrderLine(@PathVariable("id") String orderLineId) {
        orderLineService.deleteByOrderLineId(orderLineId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/orderline/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllOrderLines() {
        orderLineService.deleteAllOrderLines();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
