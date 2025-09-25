package com.example.optimisticlock.repository;

import com.example.optimisticlock.entity.BaseEntity;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// BaseRepositoryに定義されたアノテーション設定を検証するテスト。
@DisplayName("BaseRepositoryのアノテーション仕様")
class BaseRepositoryAnnotationTest {

    private final Class<BaseRepository> repositoryType = BaseRepository.class;

    @Nested
    @DisplayName("insert")
    class Insert {
        @Test
        @DisplayName("正常系")
        void hasInsertProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("insert", BaseEntity.class);
            InsertProvider provider = method.getAnnotation(InsertProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("正常系")
        void hasUpdateProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("update", BaseEntity.class);
            UpdateProvider provider = method.getAnnotation(UpdateProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("正常系")
        void hasDeleteProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("delete", BaseEntity.class);
            DeleteProvider provider = method.getAnnotation(DeleteProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("正常系")
        void hasSelectProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("findById", BaseEntity.class);
            SelectProvider provider = method.getAnnotation(SelectProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("正常系")
        void hasSelectProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("findAll");
            SelectProvider provider = method.getAnnotation(SelectProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }

    @Nested
    @DisplayName("checkUpdate")
    class CheckUpdate {
        @Test
        @DisplayName("正常系")
        void hasSelectProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("checkUpdate", BaseEntity.class);
            SelectProvider provider = method.getAnnotation(SelectProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }

    @Nested
    @DisplayName("checkUpdateList")
    class CheckUpdateList {
        @Test
        @DisplayName("正常系")
        void hasSelectProvider() throws NoSuchMethodException {
            Method method = repositoryType.getMethod("checkUpdateList", List.class);
            SelectProvider provider = method.getAnnotation(SelectProvider.class);
            assertNotNull(provider);
            assertEquals(BaseRepositoryProvider.class, provider.type());
        }
    }
}
