package com.example.optimisticlock.exception;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(String message) {
        super(message);
    }
}
