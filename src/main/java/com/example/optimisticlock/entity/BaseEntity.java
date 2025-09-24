package com.example.optimisticlock.entity;

import lombok.Data;

import java.time.LocalDateTime;

// 監査情報やバージョン管理など全エンティティ共通の属性を保持する基底クラス。
@Data
public abstract class BaseEntity {

    // 最終更新日時を格納する共通フィールド。
    private LocalDateTime zzcmnFdate;

    // 楽観ロック用のバージョン番号。
    private Integer version;
}
