package com.isa.pad.marketwarehouse.controller;

import com.isa.pad.marketwarehouse.model.Product;
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

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Faust on 12/20/2017.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public HttpEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = productService.findAll();
        if (allProducts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products")));
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductById(p.getProductId())).withSelfRel()));
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/", method = RequestMethod.GET, params = "name")
    public HttpEntity<?> getProductByName(@RequestParam("name") String name) {
        List<Product> allProducts = productService.findByName(name);
        if (allProducts.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No products were found with name =" + name);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductByName(name)).withRel("products")));
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductById(p.getProductId())).withSelfRel()));
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    public HttpEntity<?> getProductById(@PathVariable("id") String productId) {
        Product byProductId = productService.findByProductId(productId);
        if (byProductId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No product was found with id =" + productId);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            byProductId.add(linkTo(methodOn(ProductController.class).getProductById(productId)).withSelfRel());
            return new ResponseEntity<>(byProductId, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/", method = RequestMethod.GET, params = "q")
    public ResponseEntity<?> getProductByAnyField(@RequestParam("q") String q) {
        List<Product> allProducts = productService.findByAnyField(q);
        if (allProducts.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No such products with requested query = " + q);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductByAnyField(q)).withRel("query")));
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductById(p.getProductId())).withSelfRel()));
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/", method = RequestMethod.GET, params = "code_starts_with")
    public ResponseEntity<?> getProductByCodeStartsWith(@RequestParam("code_starts_with") String text) {
        List<Product> allProducts = productService.findByCodeStartingWith(text);
        if (allProducts.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "No such products which start with = " + text);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        } else {
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductByCodeStartsWith(text)).withRel("startsWith")));
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductById(p.getProductId())).withSelfRel()));
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/", method = RequestMethod.GET, params = {"start", "end"})
    public ResponseEntity<?> getAllProductsByLimit(@RequestParam("start") int start, @RequestParam("end") int end) {
        List<Product> allProducts = productService.findAllByLimit(start, end);
        if (allProducts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getAllProductsByLimit(start, end)).withRel("paging")));
            allProducts.forEach(p -> p.add(linkTo(methodOn(ProductController.class).getProductById(p.getProductId())).withSelfRel()));
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/", method = RequestMethod.POST)
    public ResponseEntity<?> createProduct(@RequestHeader(value = "Content-Type") String contentType, @RequestBody String body, UriComponentsBuilder uriComponentsBuilder) {
        if (contentType.contains("xml")) {
            XmlValidator xmlValidator = new XmlValidator("product_schema.xsd", Product.class);
            if (xmlValidator.validate(body)) {
                XmlSerializer xmlSerializer = new XmlSerializer();
                Product p = xmlSerializer.fromXml(body, Product.class);
                return createProductFromSerialized(uriComponentsBuilder, p);
            } else {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("X-ErrorResponse", "XML Validation failed with : " + xmlValidator.getMessage());
                return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
            }
        } else if (contentType.contains("json")) {
            JsonValidator jsonValidator = new JsonValidator("product_schema.json");
            if (jsonValidator.validate(body)) {
                JsonSerializer jsonSerializer = new JsonSerializer();
                Product p = jsonSerializer.fromJson(body, Product.class);
                return createProductFromSerialized(uriComponentsBuilder, p);
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

    private ResponseEntity<?> createProductFromSerialized(UriComponentsBuilder uriComponentsBuilder, Product p) {
        if (productService.productExists(p)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to create product. Already exists.");
            return new ResponseEntity<>(httpHeaders, HttpStatus.CONFLICT);
        } else {
            Product product = productService.saveProduct(p);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uriComponentsBuilder.path("/products/product/{id}").buildAndExpand(product.getProductId()).toUri());
            return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/product/{id}", method = RequestMethod.PUT, consumes = {"application/json", "application/xml", "application/yaml"})
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody Product p) {
        Product foundProduct = productService.findByProductId(id);
        if (foundProduct != null) {
            foundProduct.setName(StringUtils.isEmpty(p.getName()) ? foundProduct.getName() : p.getName());
            foundProduct.setCode(StringUtils.isEmpty(p.getCode()) ? foundProduct.getCode() : p.getCode());
            foundProduct.setUnitPrice(StringUtils.isEmpty(p.getUnitPrice()) ? foundProduct.getUnitPrice() : p.getUnitPrice());
            productService.updateProduct(foundProduct);
            foundProduct.add(linkTo(methodOn(ProductController.class).getProductById(foundProduct.getProductId())).withSelfRel());
            return new ResponseEntity<>(foundProduct, HttpStatus.OK);
        } else {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-ErrorResponse", "Unable to update. No such product with id = " + id);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String productId) {
        productService.deleteByProductId(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/product/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllProducts() {
        productService.deleteAllProducts();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
