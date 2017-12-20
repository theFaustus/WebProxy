package com.isa.pad.marketwarehouse.model;

/**
 * Created by Faust on 12/20/2017.
 */
public class OrderLine {
    private String id;
    private Product product;

    public OrderLine(Product product) {
        this.product = product;
    }

    public OrderLine() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderLine orderLine = (OrderLine) o;

        if (getId() != null ? !getId().equals(orderLine.getId()) : orderLine.getId() != null) return false;
        return getProduct() != null ? getProduct().equals(orderLine.getProduct()) : orderLine.getProduct() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getProduct() != null ? getProduct().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "id='" + id + '\'' +
                ", product=" + product +
                '}';
    }
}
