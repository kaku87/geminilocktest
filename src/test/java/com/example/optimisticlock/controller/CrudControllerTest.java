package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.service.CrudService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// CrudControllerのデフォルトメソッドの挙動を検証する単体テスト。
@DisplayName("CrudControllerのデフォルト実装")
class CrudControllerTest {

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("正常系")
        void normal() {
            CrudService<DummyEntity, ?> service = mock(CrudService.class);
            DummyEntity entity = new DummyEntity();

            ResponseEntity<DummyEntity> response = controller(service).create(entity);

            verify(service).insert(entity);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertSame(entity, response.getBody());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("正常系")
        void normal() {
            CrudService<DummyEntity, ?> service = mock(CrudService.class);
            DummyEntity entity = new DummyEntity();
            when(service.findById(entity)).thenReturn(entity);

            ResponseEntity<DummyEntity> response = controller(service).findById(entity);

            verify(service).findById(entity);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(entity, response.getBody());
        }

        @Test
        @DisplayName("対象未存在_異常系")
        void notFound() {
            CrudService<DummyEntity, ?> service = mock(CrudService.class);
            DummyEntity entity = new DummyEntity();
            when(service.findById(entity)).thenReturn(null);

            ResponseEntity<DummyEntity> response = controller(service).findById(entity);

            verify(service).findById(entity);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("正常系")
        void normal() {
            CrudService<DummyEntity, ?> service = mock(CrudService.class);
            DummyEntity entity = new DummyEntity();
            List<DummyEntity> entities = Collections.singletonList(entity);
            when(service.findAll()).thenReturn(entities);

            ResponseEntity<List<DummyEntity>> response = controller(service).getAll();

            verify(service).findAll();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(entities, response.getBody());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("正常系")
        void normal() {
            CrudService<DummyEntity, ?> service = mock(CrudService.class);
            DummyEntity entity = new DummyEntity();

            ResponseEntity<DummyEntity> response = controller(service).update(entity);

            verify(service).update(entity);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(entity, response.getBody());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("正常系")
        void normal() {
            CrudService<DummyEntity, ?> service = mock(CrudService.class);
            DummyEntity entity = new DummyEntity();

            ResponseEntity<Void> response = controller(service).delete(entity);

            verify(service).delete(entity);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    private CrudController<DummyEntity> controller(CrudService<DummyEntity, ?> service) {
        return new CrudController<DummyEntity>() {
            @Override
            public CrudService<DummyEntity, ?> getService() {
                return service;
            }
        };
    }

    private static class DummyEntity extends BaseEntity {
        private Long id;

        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }
    }
}
