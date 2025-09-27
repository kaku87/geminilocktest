package com.example.optimisticlock.controller;

import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.repository.BaseRepository;
import com.example.optimisticlock.service.CrudService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// CrudControllerのデフォルトメソッドの挙動を検証する単体テスト。
@DisplayName("CrudControllerのデフォルト実装")
class CrudControllerTest {

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("正常系")
        void normal() {
            StubCrudService service = new StubCrudService();
            DummyEntity entity = new DummyEntity();

            ResponseEntity<DummyEntity> response = controller(service).create(entity);

            assertTrue(service.repository.insertCalled);
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
            StubCrudService service = new StubCrudService();
            DummyEntity entity = new DummyEntity();
            service.repository.findByIdResult = entity;

            ResponseEntity<DummyEntity> response = controller(service).findById(entity);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(entity, response.getBody());
        }

        @Test
        @DisplayName("対象未存在_異常系")
        void notFound() {
            StubCrudService service = new StubCrudService();
            DummyEntity entity = new DummyEntity();
            service.repository.findByIdResult = null;

            ResponseEntity<DummyEntity> response = controller(service).findById(entity);

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
            StubCrudService service = new StubCrudService();
            DummyEntity entity = new DummyEntity();
            List<DummyEntity> entities = Collections.singletonList(entity);
            service.repository.findAllResult = entities;

            ResponseEntity<List<DummyEntity>> response = controller(service).getAll();

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
            StubCrudService service = new StubCrudService();
            DummyEntity entity = new DummyEntity();

            ResponseEntity<DummyEntity> response = controller(service).update(entity);

            assertTrue(service.repository.updateCalled);
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
            StubCrudService service = new StubCrudService();
            DummyEntity entity = new DummyEntity();

            ResponseEntity<Void> response = controller(service).delete(entity);

            assertTrue(service.repository.deleteCalled);
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

    private static class StubCrudService implements CrudService<DummyEntity, BaseRepository<DummyEntity>> {
        private final StubRepository repository = new StubRepository();

        @Override
        public BaseRepository<DummyEntity> getRepository() {
            return repository;
        }
    }

    private static class StubRepository implements BaseRepository<DummyEntity> {
        boolean insertCalled;
        boolean updateCalled;
        boolean deleteCalled;
        DummyEntity findByIdResult;
        List<DummyEntity> findAllResult = Collections.emptyList();

        @Override
        public int insert(DummyEntity entity) {
            insertCalled = true;
            return 1;
        }

        @Override
        public int update(DummyEntity entity) {
            updateCalled = true;
            return 1;
        }

        @Override
        public int delete(DummyEntity entity) {
            deleteCalled = true;
            return 1;
        }

        @Override
        public DummyEntity findById(DummyEntity entity) {
            return findByIdResult;
        }

        @Override
        public List<DummyEntity> findAll() {
            return findAllResult;
        }

        @Override
        public int checkUpdate(DummyEntity entity) {
            return 1;
        }

        @Override
        public int checkUpdateList(List<DummyEntity> entities) {
            return entities.size();
        }
    }

    private static class DummyEntity extends BaseEntity {
        @SuppressWarnings("unused")
        private Long id;
    }
}
