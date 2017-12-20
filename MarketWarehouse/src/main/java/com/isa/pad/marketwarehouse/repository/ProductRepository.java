package com.isa.pad.marketwarehouse.repository;

import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface ProductRepository extends MongoRepository<Product, String>{
    List<Product> findByName(String name);
    Product findByCode(String code);
    List<Product> findByNameIgnoreCaseContainingOrUnitPriceIgnoreCaseContainingOrCodeIgnoreCaseContaining(String text);
}
