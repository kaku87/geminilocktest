package com.example.optimisticlock.repository;

import com.example.optimisticlock.entity.BaseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface BaseRepository<T extends BaseEntity> {

    @InsertProvider(type = BaseRepositoryProvider.class)
    
    int insert(T entity);

    @UpdateProvider(type = BaseRepositoryProvider.class)
    int update(T entity);

    @DeleteProvider(type = BaseRepositoryProvider.class)
    int delete(T entity);

    @SelectProvider(type = BaseRepositoryProvider.class)
    T findById(T entity);

    @SelectProvider(type = BaseRepositoryProvider.class)
    List<T> findAll();

    @SelectProvider(type = BaseRepositoryProvider.class)
    int checkUpdate(T entity);

    @SelectProvider(type = BaseRepositoryProvider.class)
    int checkUpdateList(List<T> entities);
}
