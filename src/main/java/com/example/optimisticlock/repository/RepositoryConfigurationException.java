package com.example.optimisticlock.repository;

// BaseRepositoryProviderの設定不備を表す独自例外。
public class RepositoryConfigurationException extends RuntimeException {

    public RepositoryConfigurationException(String message) {
        super(message);
    }
}
