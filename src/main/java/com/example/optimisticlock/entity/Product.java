package com.example.optimisticlock.entity;

import com.example.optimisticlock.annotation.TableName;
import javax.persistence.Id;

@TableName("product")
public class Product extends BaseEntity {

    @Id
    private Long id;

    @Id
    private String tenantId;

    private String name;
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}