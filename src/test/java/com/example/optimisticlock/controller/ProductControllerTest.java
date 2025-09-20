package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.Product;
import com.example.optimisticlock.exception.OptimisticLockException;
import com.example.optimisticlock.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetProduct() throws Exception {
        when(productService.getProduct(1L, "default")).thenReturn(product);

        mockMvc.perform(get("/products/default/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testCreate() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    @Test
    void testFindById() throws Exception {
        when(productService.findById(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        when(productService.findById(any(Product.class))).thenReturn(null);

        mockMvc.perform(post("/products/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        when(productService.findAll()).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void testUpdate() throws Exception {
        mockMvc.perform(put("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testHandleOptimisticLockException() throws Exception {
        doThrow(new OptimisticLockException("Locking error")).when(productService).update(any(Product.class));

        mockMvc.perform(put("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Locking error"));
    }
}
