package com.example.optimisticlock.controller;

import com.example.optimisticlock.exception.OptimisticLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

// GlobalExceptionHandlerのレスポンス内容を検証するテスト。
@DisplayName("GlobalExceptionHandlerの変換処理")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("handleOptimisticLockException")
    class HandleOptimisticLockException {

        @Test
        @DisplayName("正常系")
        void normal() {
            OptimisticLockException ex = new OptimisticLockException("Optimistic conflict");

            ResponseEntity<String> response = handler.handleOptimisticLockException(ex);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Optimistic conflict", response.getBody());
            assertSame(String.class, response.getBody().getClass());
        }
    }
}
