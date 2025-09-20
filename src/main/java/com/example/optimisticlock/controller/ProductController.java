package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.Product;
import com.example.optimisticlock.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController extends GlobalExceptionHandler implements CrudController<Product> {

    @Autowired
    private ProductService productService;

    @GetMapping("/{tenantId}/{id}")
    public Product getProduct(@PathVariable String tenantId, @PathVariable Long id) {
        return productService.getProduct(id, tenantId);
    }

    

    @Override
    public ProductService getService() {
        return productService;
    }
}
