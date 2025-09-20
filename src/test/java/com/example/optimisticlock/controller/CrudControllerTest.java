package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.Product;
import com.example.optimisticlock.service.CrudService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrudControllerTest {

    @Test
    void createDelegatesToServiceAndReturnsCreated() {
        CrudService<Product, ?> service = mock(CrudService.class);
        Product product = new Product();

        ResponseEntity<Product> response = controller(service).create(product);

        verify(service).insert(product);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(product, response.getBody());
    }

    @Test
    void findByIdReturnsEntityWhenPresent() {
        CrudService<Product, ?> service = mock(CrudService.class);
        Product product = new Product();
        when(service.findById(product)).thenReturn(product);

        ResponseEntity<Product> response = controller(service).findById(product);

        verify(service).findById(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(product, response.getBody());
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() {
        CrudService<Product, ?> service = mock(CrudService.class);
        Product product = new Product();
        when(service.findById(product)).thenReturn(null);

        ResponseEntity<Product> response = controller(service).findById(product);

        verify(service).findById(product);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllReturnsListFromService() {
        CrudService<Product, ?> service = mock(CrudService.class);
        Product product = new Product();
        List<Product> products = Collections.singletonList(product);
        when(service.findAll()).thenReturn(products);

        ResponseEntity<List<Product>> response = controller(service).getAll();

        verify(service).findAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    void updateDelegatesToServiceAndReturnsOk() {
        CrudService<Product, ?> service = mock(CrudService.class);
        Product product = new Product();

        ResponseEntity<Product> response = controller(service).update(product);

        verify(service).update(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(product, response.getBody());
    }

    @Test
    void deleteDelegatesToServiceAndReturnsNoContent() {
        CrudService<Product, ?> service = mock(CrudService.class);
        Product product = new Product();

        ResponseEntity<Void> response = controller(service).delete(product);

        verify(service).delete(product);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    private CrudController<Product> controller(CrudService<Product, ?> service) {
        return new CrudController<Product>() {
            @Override
            public CrudService<Product, ?> getService() {
                return service;
            }
        };
    }
}

