package com.example.optimisticlock.service;

import com.example.optimisticlock.entity.Product;
import com.example.optimisticlock.exception.OptimisticLockException;
import com.example.optimisticlock.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService implements CrudService<Product, ProductRepository> {

    @Autowired
    private ProductRepository productRepository;

    public Product getProduct(Long id, String tenantId) {
        return productRepository.getProduct(id, tenantId);
    }

    @Transactional
    public void updateProduct(Product product) {
        if (productRepository.checkUpdate(product) == 0) {
            throw new OptimisticLockException("Failed to update product due to optimistic lock");
        }
        productRepository.update(product);
    }

    @Override
    public ProductRepository getRepository() {
        return this.productRepository;
    }
}
