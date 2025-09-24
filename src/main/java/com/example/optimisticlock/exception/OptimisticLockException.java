package com.example.optimisticlock.exception;

// 楽観ロックが衝突した際に投げられるドメイン例外。
public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(String message) {
        super(message);
    }
}
