package com.isa.pad.marketwarehouse.repository;

import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.OrderLine;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface OrderLineRepository extends MongoRepository<OrderLine, String>{
}
