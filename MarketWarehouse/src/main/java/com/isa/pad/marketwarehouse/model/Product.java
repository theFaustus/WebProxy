package com.isa.pad.marketwarehouse.model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.annotation.Id;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Created by Faust on 12/20/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("product")
@XmlRootElement(name = "product")
public class Product extends ResourceSupport {
    @Id
    private String productId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("unitprice")
    private BigDecimal unitPrice;
    @JsonProperty("code")
    private String code;

    public Product() {
    }

    public Product(String name, BigDecimal unitPrice, String code) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.code = code;
    }

    public Product(String productId, String name, BigDecimal unitPrice, String code) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.code = code;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

        if (getProductId() != null ? !getProductId().equals(product.getProductId()) : product.getProductId() != null) return false;
        if (getName() != null ? !getName().equals(product.getName()) : product.getName() != null) return false;
        if (getUnitPrice() != null ? !getUnitPrice().equals(product.getUnitPrice()) : product.getUnitPrice() != null)
            return false;
        return getCode() != null ? getCode().equals(product.getCode()) : product.getCode() == null;

    }

    @Override
    public int hashCode() {
        int result = getProductId() != null ? getProductId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getUnitPrice() != null ? getUnitPrice().hashCode() : 0);
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", code='" + code + '\'' +
                '}';
    }


}
