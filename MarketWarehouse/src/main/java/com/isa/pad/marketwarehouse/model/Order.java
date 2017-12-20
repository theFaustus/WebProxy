package com.isa.pad.marketwarehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Faust on 12/20/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("order")
@XmlRootElement(name = "order")
public class Order extends ResourceSupport{
    @Id
    private String orderId;
    @JsonProperty("customer")
    @DBRef
    private Customer customer;
    @JsonProperty("orderlinelist")
    @DBRef
    private List<OrderLine> orderLineList = new ArrayList<>();
    @JsonProperty("date")
    private Date orderDate = new Date();

    public Order() {
    }

    public Order(Customer customer) {
        this.customer = customer;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderLine> getOrderLineList() {
        return orderLineList;
    }

    public void setOrderLineList(List<OrderLine> orderLineList) {
        this.orderLineList = orderLineList;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void add(OrderLine o){
        orderLineList.add(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (getOrderId() != null ? !getOrderId().equals(order.getOrderId()) : order.getOrderId() != null) return false;
        if (getCustomer() != null ? !getCustomer().equals(order.getCustomer()) : order.getCustomer() != null)
            return false;
        if (getOrderLineList() != null ? !getOrderLineList().equals(order.getOrderLineList()) : order.getOrderLineList() != null)
            return false;
        return getOrderDate() != null ? getOrderDate().equals(order.getOrderDate()) : order.getOrderDate() == null;

    }

    @Override
    public int hashCode() {
        int result = getOrderId() != null ? getOrderId().hashCode() : 0;
        result = 31 * result + (getCustomer() != null ? getCustomer().hashCode() : 0);
        result = 31 * result + (getOrderLineList() != null ? getOrderLineList().hashCode() : 0);
        result = 31 * result + (getOrderDate() != null ? getOrderDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customer=" + customer +
                ", orderLineList=" + orderLineList +
                ", orderDate=" + orderDate +
                '}';
    }
}
