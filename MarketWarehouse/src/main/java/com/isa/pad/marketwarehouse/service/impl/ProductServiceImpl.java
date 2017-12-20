package com.isa.pad.marketwarehouse.service.impl;

import com.google.common.collect.Lists;
import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.Order;
import com.isa.pad.marketwarehouse.model.Product;
import com.isa.pad.marketwarehouse.repository.CustomerRepository;
import com.isa.pad.marketwarehouse.repository.ProductRepository;
import com.isa.pad.marketwarehouse.service.CustomerService;
import com.isa.pad.marketwarehouse.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public Product findByProductId(String productId) {
        return productRepository.findOne(productId);
    }

    @Override
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> findByAnyField(String q) {
        return productRepository.findByNameIgnoreCaseContainingOrUnitPriceIgnoreCaseContainingOrCodeIgnoreCaseContaining(q);
    }

    @Override
    public List<Product> findByCodeStartingWith(String text) {
        return productRepository.findByCodeStartingWith(text);
    }

    @Override
    public List<Product> findAllByLimit(int startIndex, int endIndex) {
        Pageable pageable = new PageRequest(startIndex, endIndex);
        Page<Product> all = productRepository.findAll(pageable);
        return Lists.newArrayList(all);
    }

    @Override
    public Product saveProduct(Product p) {
        return productRepository.save(p);
    }

    @Override
    public void updateProduct(Product p) {
        productRepository.save(p);
    }

    @Override
    public void deleteByProductId(String productId) {
        productRepository.delete(productId);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public boolean productExists(Product p) {
        return productRepository.exists(Example.of(p));
    }

    @Override
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }
}
