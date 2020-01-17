package com.javaxpert.demos.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A simple entity to show some Hibernate behaviour
 * to be tester by Test classes...
 */
@Entity
@Table(name = "product_tbl")
@Cacheable
public class Product  implements Serializable {
    @Id
    private String productId;

    @Column
    private String description;

    @Column
    private String productName;

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return getProductId().equals(product.getProductId()) &&
                Objects.equals(description, product.description) &&
                productName.equals(product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), description, productName);
    }
}
