package com.example.optimisticlock.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

// propertiesファイルからメッセージを取得するユーティリティ。
public final class Messages {

    private static final String BUNDLE_NAME = "messages";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String get(String key, Object... args) {
        try {
            String pattern = BUNDLE.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (MissingResourceException ex) {
            return '!' + key + '!';
        }
    }
}
