package com.isa.pad.marketwarehouse.service;

import com.isa.pad.marketwarehouse.model.Product;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface ProductService {
    Product findByProductId(String productId);

    List<Product> findByName(String name);

    List<Product> findByAnyField(String q);

    List<Product> findByCodeStartingWith(String text);

    List<Product> findAllByLimit(int startIndex, int endIndex);

    Product saveProduct(Product p);

    void updateProduct(Product p);

    void deleteByProductId(String productId);

    List<Product> findAll();

    boolean productExists(Product p);

    void deleteAllProducts();
}
