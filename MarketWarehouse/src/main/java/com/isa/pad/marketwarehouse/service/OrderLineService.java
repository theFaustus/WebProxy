package com.isa.pad.marketwarehouse.service;

import com.isa.pad.marketwarehouse.model.OrderLine;

import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
public interface OrderLineService {
    OrderLine findByOrderLineId(String orderLineId);

    List<OrderLine> findAllByLimit(int startIndex, int endIndex);

    OrderLine saveOrderLine(OrderLine o);

    void updateOrderLine(OrderLine o);

    void deleteByOrderLineId(String orderLineId);

    List<OrderLine> findAll();

    boolean orderLineExists(OrderLine o);

    void deleteAllOrderLines();
}
