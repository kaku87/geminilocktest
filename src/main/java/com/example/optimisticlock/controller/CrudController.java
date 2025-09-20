package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.service.CrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CrudController<T extends BaseEntity> {

    CrudService<T, ?> getService();

    @PostMapping
    default ResponseEntity<T> create(@RequestBody T entity) {
        getService().insert(entity);
        return new ResponseEntity<>(entity, HttpStatus.CREATED);
    }

    @PostMapping("/query")
    default ResponseEntity<T> findById(@RequestBody T entity) {
        T foundEntity = getService().findById(entity);
        if (foundEntity != null) {
            return new ResponseEntity<>(foundEntity, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    default ResponseEntity<List<T>> getAll() {
        List<T> entities = getService().findAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @PutMapping
    default ResponseEntity<T> update(@RequestBody T entity) {
        getService().update(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @DeleteMapping
    default ResponseEntity<Void> delete(@RequestBody T entity) {
        getService().delete(entity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
