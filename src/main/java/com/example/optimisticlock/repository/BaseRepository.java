package com.example.optimisticlock.repository;

import com.example.optimisticlock.entity.BaseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

// 全エンティティに共通するCRUD操作を定義するマッパーインタフェース。
public interface BaseRepository<T extends BaseEntity> {

    @InsertProvider(type = BaseRepositoryProvider.class)
    /**
     * エンティティを新規登録する。
     * @param entity 登録対象
     * @return 影響件数
     */
    int insert(T entity);

    @UpdateProvider(type = BaseRepositoryProvider.class)
    /**
     * エンティティを更新する。
     * @param entity 更新対象
     * @return 影響件数
     */
    int update(T entity);

    @DeleteProvider(type = BaseRepositoryProvider.class)
    /**
     * エンティティを削除する。
     * @param entity 削除対象
     * @return 影響件数
     */
    int delete(T entity);

    @SelectProvider(type = BaseRepositoryProvider.class)
    /**
     * ID条件で単一エンティティを取得する。
     * @param entity 検索条件となるエンティティ
     * @return 該当エンティティ
     */
    T findById(T entity);

    @SelectProvider(type = BaseRepositoryProvider.class)
    /**
     * 全件取得を行う。
     * @return エンティティ一覧
     */
    List<T> findAll();

    @SelectProvider(type = BaseRepositoryProvider.class)
    /**
     * 単一エンティティの楽観ロック条件を検証する。
     * @param entity 検証対象
     * @return 条件に合致した件数
     */
    int checkUpdate(T entity);

    @SelectProvider(type = BaseRepositoryProvider.class)
    /**
     * 複数エンティティの楽観ロック条件をまとめて検証する。
     * @param entities 検証対象リスト
     * @return 条件に合致した件数
     */
    int checkUpdateList(List<T> entities);
}
