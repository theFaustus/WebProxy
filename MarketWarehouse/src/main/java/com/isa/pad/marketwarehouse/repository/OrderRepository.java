package com.isa.pad.marketwarehouse.repository;

import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface OrderRepository extends MongoRepository<Order, String>{
    @Query(value = "{ 'customer.$customerId' : ?0 }")
    List<Order> findByCustomerId(String customerId);
}
