package com.example.optimisticlock.repository;

import com.example.optimisticlock.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testInsert() {
        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setTenantId("default");
        newProduct.setName("New Product");
        newProduct.setQuantity(100);
        newProduct.setVersion(0);
        newProduct.setZzcmnFdate(LocalDateTime.now());

        productRepository.insert(newProduct);

        Product foundProduct = productRepository.getProduct(2L, "default");
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("New Product");
    }

    @Test
    void testUpdate() {
        Product product = productRepository.getProduct(1L, "default");
        assertThat(product).isNotNull();
        Integer oldVersion = product.getVersion();
        product.setQuantity(50);

        int updatedRows = productRepository.update(product);
        assertThat(updatedRows).isEqualTo(1);

        Product updatedProduct = productRepository.getProduct(1L, "default");
        assertThat(updatedProduct.getQuantity()).isEqualTo(50);
        assertThat(updatedProduct.getVersion()).isEqualTo(oldVersion + 1);
    }

    @Test
    void testDelete() {
        Product product = productRepository.getProduct(1L, "default");
        assertThat(product).isNotNull();

        int deletedRows = productRepository.delete(product);
        assertThat(deletedRows).isEqualTo(1);

        Product deletedProduct = productRepository.getProduct(1L, "default");
        assertThat(deletedProduct).isNull();
    }

    @Test
    void testFindById() {
        Product product = new Product();
        product.setId(1L);
        product.setTenantId("default");

        Product foundProduct = productRepository.findById(product);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
    }

    @Test
    void testFindAll() {
        List<Product> products = productRepository.findAll();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
    }

    @Test
    void testGetProduct() {
        Product product = productRepository.getProduct(1L, "default");
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("Test Product");
    }

    @Test
    void testCheckUpdate() {
        Product product = productRepository.getProduct(1L, "default");
        assertThat(product).isNotNull();

        int count = productRepository.checkUpdate(product);
        assertThat(count).isEqualTo(1);

        product.setVersion(product.getVersion() + 1);
        count = productRepository.checkUpdate(product);
        assertThat(count).isEqualTo(0);
    }
}