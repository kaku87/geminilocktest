package com.example.optimisticlock.repository;

import com.example.optimisticlock.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductRepository extends BaseRepository<Product> {

    @Select("SELECT id, tenant_id, name, quantity, version, zzcmn_fdate FROM product WHERE id = #{id} AND tenant_id = #{tenantId}")
    Product getProduct(@Param("id") Long id, @Param("tenantId") String tenantId);
}
