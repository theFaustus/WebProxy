package com.isa.pad.marketwarehouse.service;

import com.isa.pad.marketwarehouse.model.Order;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface OrderService {
    Order findByOrderId(String orderId);

    List<Order> findByAllByCustomerId(String customerId);

    List<Order> findAllByLimit(int startIndex, int endIndex);

    Order saveOrder(Order o);

    void updateOrder(Order o);

    void deleteByOrderId(String orderId);

    List<Order> findAll();

    boolean orderExists(Order o);

    void deleteAllOrders();
}
