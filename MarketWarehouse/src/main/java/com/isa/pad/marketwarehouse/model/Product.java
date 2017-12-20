package com.isa.pad.marketwarehouse.model;

import java.math.BigDecimal;

/**
 * Created by Faust on 12/20/2017.
 */
public class Product {

    private String id;

    private String name;

    private BigDecimal unitPrice;

    private String code;

    public Product() {
    }

    public Product(String id, String name, BigDecimal unitPrice, String code) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (getId() != null ? !getId().equals(product.getId()) : product.getId() != null) return false;
        if (getName() != null ? !getName().equals(product.getName()) : product.getName() != null) return false;
        if (getUnitPrice() != null ? !getUnitPrice().equals(product.getUnitPrice()) : product.getUnitPrice() != null)
            return false;
        return getCode() != null ? getCode().equals(product.getCode()) : product.getCode() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getUnitPrice() != null ? getUnitPrice().hashCode() : 0);
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", code='" + code + '\'' +
                '}';
    }


}
