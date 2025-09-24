package com.example.optimisticlock.service;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.exception.OptimisticLockException;
import com.example.optimisticlock.repository.BaseRepository;

import java.util.List;

// 汎用的なCRUD処理をサービス層で再利用するための共通インタフェース。
public interface CrudService<T extends BaseEntity, R extends BaseRepository<T>> {

    /**
     * このサービスが利用するリポジトリを取得する。
     * @return CRUD処理対象のリポジトリ
     */
    R getRepository();

    /**
     * エンティティを新規登録する。
     * @param entity 登録対象
     */
    default void insert(T entity) {
        getRepository().insert(entity);
    }

    /**
     * エンティティを更新する。
     * @param entity 更新対象
     * @throws OptimisticLockException 楽観ロックに失敗した場合
     */
    default void update(T entity) {
        int result = getRepository().update(entity);
        if (result == 0) {
            throw new OptimisticLockException("Update failed, data has been modified by others");
        }
    }

    /**
     * エンティティを削除する。
     * @param entity 削除対象
     * @throws OptimisticLockException 楽観ロックに失敗した場合
     */
    default void delete(T entity) {
        int result = getRepository().delete(entity);
        if (result == 0) {
            throw new OptimisticLockException("Delete failed, data has been modified by others");
        }
    }

    /**
     * ID条件で単一エンティティを取得する。
     * @param entity 検索条件
     * @return 該当エンティティ
     */
    default T findById(T entity) {
        return getRepository().findById(entity);
    }

    /**
     * 全件取得を行う。
     * @return エンティティ一覧
     */
    default List<T> findAll() {
        return getRepository().findAll();
    }

    /**
     * リスト更新前に楽観ロック条件を検証する。
     * @param entities 検証対象
     * @throws OptimisticLockException 楽観ロックに失敗した場合
     */
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
