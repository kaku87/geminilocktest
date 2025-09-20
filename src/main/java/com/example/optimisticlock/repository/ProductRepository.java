package com.example.optimisticlock.repository;

import com.example.optimisticlock.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
// 製品エンティティの永続化を担当するMyBatisマッパー。BaseRepositoryの汎用操作を継承する。
public interface ProductRepository extends BaseRepository<Product> {

    @Select("SELECT id, tenant_id, name, quantity, version, zzcmn_fdate FROM product WHERE id = #{id} AND tenant_id = #{tenantId}")
    /**
     * 主キーとテナントIDを使って単一の製品を取得するカスタムクエリ。
     * @param id 製品ID
     * @param tenantId テナント識別子
     * @return 条件に一致する製品
     */
    Product getProduct(@Param("id") Long id, @Param("tenantId") String tenantId);
}
