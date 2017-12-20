package com.isa.pad.marketwarehouse.service.impl;

import com.google.common.collect.Lists;
import com.isa.pad.marketwarehouse.model.Customer;
import com.isa.pad.marketwarehouse.model.OrderLine;
import com.isa.pad.marketwarehouse.repository.CustomerRepository;
import com.isa.pad.marketwarehouse.repository.OrderLineRepository;
import com.isa.pad.marketwarehouse.service.CustomerService;
import com.isa.pad.marketwarehouse.service.OrderLineService;
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
public class OrderLineServiceImpl implements OrderLineService {

    @Autowired
    OrderLineRepository orderLineRepository;

    @Override
    public OrderLine findByOrderLineId(String orderLineId) {
        return orderLineRepository.findOne(orderLineId);
    }

    @Override
    public List<OrderLine> findAllByLimit(int startIndex, int endIndex) {
        Pageable pageable = new PageRequest(startIndex, endIndex);
        Page<OrderLine> all = orderLineRepository.findAll(pageable);
        return Lists.newArrayList(all);
    }

    @Override
    public OrderLine saveOrderLine(OrderLine o) {
        return orderLineRepository.save(o);
    }

    @Override
    public void updateOrderLine(OrderLine o) {
        orderLineRepository.save(o);
    }

    @Override
    public void deleteByOrderLineId(String orderLineId) {
        orderLineRepository.delete(orderLineId);
    }

    @Override
    public List<OrderLine> findAll() {
        return orderLineRepository.findAll();
    }

    @Override
    public boolean orderLineExists(OrderLine o) {
        return orderLineRepository.exists(o.getOrderLineId());
    }

    @Override
    public void deleteAllOrderLines() {
        orderLineRepository.deleteAll();
    }
}
