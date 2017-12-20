package com.isa.pad.marketwarehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Faust on 12/20/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("orderline")
@XmlRootElement(name = "orderline")
public class OrderLine extends ResourceSupport {
    @Id
    private String orderLineId;
    @JsonProperty("product")
    @DBRef
    private Product product;
    @JsonProperty("amount")
    private int amount;

    public OrderLine(Product product) {
        this.product = product;
    }

    public OrderLine() {
    }

    public String getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(String orderLineId) {
        this.orderLineId = orderLineId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderLine orderLine = (OrderLine) o;

        if (getAmount() != orderLine.getAmount()) return false;
        if (getOrderLineId() != null ? !getOrderLineId().equals(orderLine.getOrderLineId()) : orderLine.getOrderLineId() != null)
            return false;
        return getProduct() != null ? getProduct().equals(orderLine.getProduct()) : orderLine.getProduct() == null;

    }

    @Override
    public int hashCode() {
        int result = getOrderLineId() != null ? getOrderLineId().hashCode() : 0;
        result = 31 * result + (getProduct() != null ? getProduct().hashCode() : 0);
        result = 31 * result + getAmount();
        return result;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "orderLineId='" + orderLineId + '\'' +
                ", product=" + product +
                ", amount=" + amount +
                '}';
    }
}
