package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.service.CrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// このインタフェースはCRUD操作に共通するHTTPエンドポイントのデフォルト実装を提供する。
public interface CrudController<T extends BaseEntity> {

    /**
     * 具体的なサービスを提供する実装クラスが利用するエントリポイント。
     * @return 利用するCrudService
     */
    CrudService<T, ?> getService();

    @PostMapping
    /**
     * 新規登録処理。作成したエンティティを保存し、201 CREATEDを返す。
     * @param entity 登録対象のエンティティ
     * @return 登録後のエンティティとステータス
     */
    default ResponseEntity<T> create(@RequestBody T entity) {
        getService().insert(entity);
        return new ResponseEntity<>(entity, HttpStatus.CREATED);
    }

    @PostMapping("/query")
    /**
     * IDに該当するエンティティを検索し、存在すれば200、なければ404を返す。
     * @param entity 検索条件を保持したエンティティ
     * @return 該当エンティティまたは404レスポンス
     */
    default ResponseEntity<T> findById(@RequestBody T entity) {
        T foundEntity = getService().findById(entity);
        if (foundEntity != null) {
            return new ResponseEntity<>(foundEntity, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    /**
     * 全件取得処理。サービスから結果を取得して200で返す。
     * @return 登録済みエンティティの一覧とステータス
     */
    default ResponseEntity<List<T>> getAll() {
        List<T> entities = getService().findAll();
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @PutMapping
    /**
     * 更新処理。楽観ロック例外はサービス側で制御される。
     * @param entity 更新対象のエンティティ
     * @return 更新結果とステータス
     */
    default ResponseEntity<T> update(@RequestBody T entity) {
        getService().update(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @DeleteMapping
    /**
     * 削除処理。削除後は204 NO_CONTENTを返却する。
     * @param entity 削除対象のエンティティ
     * @return ステータスのみを含むレスポンス
     */
    default ResponseEntity<Void> delete(@RequestBody T entity) {
        getService().delete(entity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
