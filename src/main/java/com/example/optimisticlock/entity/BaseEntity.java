package com.example.optimisticlock.entity;

import com.example.optimisticlock.validation.EntityConfigured;
import lombok.Data;

import java.time.LocalDateTime;

// 監査情報など全エンティティ共通の属性を保持する基底クラス。
@Data
@EntityConfigured
public abstract class BaseEntity {

    // 登録者名
    private String zzcmnCname;

    // 登録日時
    private LocalDateTime zzcmnCdate;

    // 最終更新者名
    private String zzcmnFname;

    // 最終更新日時
    private LocalDateTime zzcmnFdate;
}
