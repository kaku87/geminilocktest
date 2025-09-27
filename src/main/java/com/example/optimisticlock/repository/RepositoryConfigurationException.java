package com.example.optimisticlock.repository;

import com.example.optimisticlock.util.Messages;

// BaseRepositoryProviderの設定不備を表す独自例外。
public class RepositoryConfigurationException extends RuntimeException {

    public RepositoryConfigurationException(String messageKey, Object... args) {
        super(Messages.get(messageKey, args));
    }
}
