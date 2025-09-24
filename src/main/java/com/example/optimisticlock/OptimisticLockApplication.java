package com.example.optimisticlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// アプリケーションのエントリーポイントとなるSpring Boot起動クラス。
@SpringBootApplication
public class OptimisticLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptimisticLockApplication.class, args);
    }

}
