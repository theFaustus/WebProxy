package com.isa.pad.marketwarehouse.service.impl;

import com.google.common.collect.Lists;
import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.Order;
import com.isa.pad.marketwarehouse.model.OrderLine;
import com.isa.pad.marketwarehouse.repository.CustomerRepository;
import com.isa.pad.marketwarehouse.repository.OrderRepository;
import com.isa.pad.marketwarehouse.service.CustomerService;
import com.isa.pad.marketwarehouse.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Override
    public Order findByOrderId(String orderId) {
        return orderRepository.findOne(orderId);
    }

    @Override
    public List<Order> findByAllByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> findAllByLimit(int startIndex, int endIndex) {
        Pageable pageable = new PageRequest(startIndex, endIndex);
        Page<Order> all = orderRepository.findAll(pageable);
        return Lists.newArrayList(all);
    }

    @Override
    public Order saveOrder(Order o) {
        return orderRepository.save(o);
    }

    @Override
    public void updateOrder(Order o) {
        orderRepository.save(o);
    }

    @Override
    public void deleteByOrderId(String orderId) {
        orderRepository.delete(orderId);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public boolean orderExists(Order o) {
        return orderRepository.exists(o.getOrderId());
    }

    @Override
    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }
}
