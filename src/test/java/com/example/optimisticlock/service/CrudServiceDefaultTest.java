package com.example.optimisticlock.service;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.exception.OptimisticLockException;
import com.example.optimisticlock.repository.BaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// CrudServiceのデフォルト実装を検証する専用テスト。
@DisplayName("CrudServiceのデフォルトメソッド")
class CrudServiceDefaultTest {

    private BaseRepository<DummyEntity> repository;
    private CrudService<DummyEntity, BaseRepository<DummyEntity>> service;
    private DummyEntity entity;

    @BeforeEach
    void setUp() {
        repository = mock(BaseRepository.class);
        service = new CrudService<DummyEntity, BaseRepository<DummyEntity>>() {
            @Override
            public BaseRepository<DummyEntity> getRepository() {
                return repository;
            }
        };
        entity = new DummyEntity();
    }

    @Nested
    @DisplayName("insert")
    class Insert {

        @Test
        @DisplayName("正常系")
        void normal() {
            service.insert(entity);
            verify(repository).insert(entity);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("正常系")
        void normal() {
            when(repository.update(entity)).thenReturn(1);
            assertDoesNotThrow(() -> service.update(entity));
            verify(repository).update(entity);
        }

        @Test
        @DisplayName("更新件数不一致_異常系")
        void noRowsAffected() {
            when(repository.update(entity)).thenReturn(0);
            assertThrows(OptimisticLockException.class, () -> service.update(entity));
            verify(repository).update(entity);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("正常系")
        void normal() {
            when(repository.delete(entity)).thenReturn(1);
            assertDoesNotThrow(() -> service.delete(entity));
            verify(repository).delete(entity);
        }

        @Test
        @DisplayName("削除件数不一致_異常系")
        void noRowsAffected() {
            when(repository.delete(entity)).thenReturn(0);
            assertThrows(OptimisticLockException.class, () -> service.delete(entity));
            verify(repository).delete(entity);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("正常系")
        void normal() {
            when(repository.findById(entity)).thenReturn(entity);
            assertSame(entity, service.findById(entity));
            verify(repository).findById(entity);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("正常系")
        void normal() {
            List<DummyEntity> list = Collections.singletonList(entity);
            when(repository.findAll()).thenReturn(list);
            assertEquals(list, service.findAll());
            verify(repository).findAll();
        }
    }

    @Nested
    @DisplayName("checkUpdateList")
    class CheckUpdateList {

        @Test
        @DisplayName("正常系")
        void normal() {
            List<DummyEntity> list = Collections.singletonList(entity);
            when(repository.checkUpdateList(list)).thenReturn(1);
            assertDoesNotThrow(() -> service.checkUpdateList(list));
            verify(repository).checkUpdateList(list);
        }

        @Test
        @DisplayName("空リスト_正常系")
        void emptyList() {
            service.checkUpdateList(Collections.emptyList());
            verify(repository, never()).checkUpdateList(anyList());
        }

        @Test
        @DisplayName("件数不一致_異常系")
        void mismatch() {
            List<DummyEntity> list = Collections.singletonList(entity);
            when(repository.checkUpdateList(list)).thenReturn(0);
            assertThrows(OptimisticLockException.class, () -> service.checkUpdateList(list));
            verify(repository).checkUpdateList(list);
        }
    }

    private static class DummyEntity extends BaseEntity {}
}
