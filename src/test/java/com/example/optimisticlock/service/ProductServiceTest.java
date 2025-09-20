package com.example.optimisticlock.service;

import com.example.optimisticlock.entity.Product;
import com.example.optimisticlock.exception.OptimisticLockException;
import com.example.optimisticlock.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setTenantId("default");
        product.setName("Test Product");
        product.setQuantity(10);
        product.setZzcmnFdate(LocalDateTime.now());
    }

    @Test
    void testGetProduct() {
        when(productRepository.getProduct(1L, "default")).thenReturn(product);

        Product found = productService.getProduct(1L, "default");

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(productRepository).getProduct(1L, "default");
    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.checkUpdate(any(Product.class))).thenReturn(1);
        when(productRepository.update(any(Product.class))).thenReturn(1);

        productService.updateProduct(product);

        verify(productRepository).checkUpdate(product);
        verify(productRepository).update(product);
    }

    @Test
    void testUpdateProduct_OptimisticLockException() {
        when(productRepository.checkUpdate(any(Product.class))).thenReturn(0);

        assertThrows(OptimisticLockException.class, () -> {
            productService.updateProduct(product);
        });

        verify(productRepository).checkUpdate(product);
    }

    @Test
    void testInsert() {
        productService.insert(product);
        verify(productRepository).insert(product);
    }

    @Test
    void testUpdate_Success() {
        when(productRepository.update(any(Product.class))).thenReturn(1);
        productService.update(product);
        verify(productRepository).update(product);
    }

    @Test
    void testUpdate_OptimisticLockException() {
        when(productRepository.update(any(Product.class))).thenReturn(0);
        assertThrows(OptimisticLockException.class, () -> {
            productService.update(product);
        });
        verify(productRepository).update(product);
    }

    @Test
    void testDelete_Success() {
        when(productRepository.delete(any(Product.class))).thenReturn(1);
        productService.delete(product);
        verify(productRepository).delete(product);
    }

    @Test
    void testDelete_OptimisticLockException() {
        when(productRepository.delete(any(Product.class))).thenReturn(0);
        assertThrows(OptimisticLockException.class, () -> {
            productService.delete(product);
        });
        verify(productRepository).delete(product);
    }

    @Test
    void testFindById() {
        when(productRepository.findById(any(Product.class))).thenReturn(product);
        Product found = productService.findById(product);
        assertThat(found).isNotNull();
        verify(productRepository).findById(product);
    }

    @Test
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        List<Product> products = productService.findAll();
        assertThat(products).hasSize(1);
        verify(productRepository).findAll();
    }

    @Test
    void testCheckUpdateList_Success() {
        List<Product> products = Collections.singletonList(product);
        when(productRepository.checkUpdateList(products)).thenReturn(1);
        productService.checkUpdateList(products);
        verify(productRepository).checkUpdateList(products);
    }

    @Test
    void testCheckUpdateList_OptimisticLockException() {
        List<Product> products = Collections.singletonList(product);
        when(productRepository.checkUpdateList(products)).thenReturn(0);
        assertThrows(OptimisticLockException.class, () -> {
            productService.checkUpdateList(products);
        });
        verify(productRepository).checkUpdateList(products);
    }
    
    @Test
    void testGetRepository() {
        assertThat(productService.getRepository()).isEqualTo(productRepository);
    }
}
