package com.example.optimisticlock.service;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.exception.OptimisticLockException;
import com.example.optimisticlock.repository.BaseRepository;

import java.util.List;


public interface CrudService<T extends BaseEntity, R extends BaseRepository<T>> {

    R getRepository();

    default void insert(T entity) {
        getRepository().insert(entity);
    }

    default void update(T entity) {
        int result = getRepository().update(entity);
        if (result == 0) {
            throw new OptimisticLockException("Update failed, data has been modified by others");
        }
    }

    default void delete(T entity) {
        int result = getRepository().delete(entity);
        if (result == 0) {
            throw new OptimisticLockException("Delete failed, data has been modified by others");
        }
    }

    default T findById(T entity) {
        return getRepository().findById(entity);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }

    default void checkUpdateList(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        int result = getRepository().checkUpdateList(entities);
        if (result != entities.size()) {
            throw new OptimisticLockException("Update failed, data has been modified by others");
        }
    }
}
