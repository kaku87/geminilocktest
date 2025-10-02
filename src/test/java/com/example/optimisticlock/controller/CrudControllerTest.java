package com.example.optimisticlock.controller;

import com.example.optimisticlock.annotation.TableName;
import com.example.optimisticlock.entity.BaseEntity;
import com.example.optimisticlock.repository.BaseRepository;
import com.example.optimisticlock.service.CrudService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

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
            StubCrudService<DummyEntity> service = new StubCrudService<>();
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
            StubCrudService<DummyEntity> service = new StubCrudService<>();
            DummyEntity entity = new DummyEntity();
            service.repository.findByIdResult = entity;

            ResponseEntity<DummyEntity> response = controller(service).findById(entity);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(entity, response.getBody());
        }

        @Test
        @DisplayName("対象未存在_異常系")
        void notFound() {
            StubCrudService<DummyEntity> service = new StubCrudService<>();
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
            StubCrudService<DummyEntity> service = new StubCrudService<>();
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
            StubCrudService<DummyEntity> service = new StubCrudService<>();
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
            StubCrudService<DummyEntity> service = new StubCrudService<>();
            DummyEntity entity = new DummyEntity();

            ResponseEntity<Void> response = controller(service).delete(entity);

            assertTrue(service.repository.deleteCalled);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    @DisplayName("validation")
    class ValidationCases {

        private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        @Test
        @DisplayName("@TableName欠落_異常系")
        void missingTableName() {
            NoTableEntity entity = new NoTableEntity();

            Set<ConstraintViolation<NoTableEntity>> violations = validator.validate(entity);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message -> message.contains("@TableName")));
        }

        @Test
        @DisplayName("@Id欠落_異常系")
        void missingId() {
            NoIdEntity entity = new NoIdEntity();

            Set<ConstraintViolation<NoIdEntity>> violations = validator.validate(entity);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message -> message.contains("@Id")));
        }
    }

    private <E extends BaseEntity> CrudController<E> controller(CrudService<E, ?> service) {
        return () -> service;
    }

    private static class StubCrudService<E extends BaseEntity> implements CrudService<E, BaseRepository<E>> {
        private final StubRepository<E> repository = new StubRepository<>();

        @Override
        public BaseRepository<E> getRepository() {
            return repository;
        }
    }

    private static class StubRepository<E extends BaseEntity> implements BaseRepository<E> {
        boolean insertCalled;
        boolean updateCalled;
        boolean deleteCalled;
        E findByIdResult;
        List<E> findAllResult = Collections.emptyList();

        @Override
        public int insert(E entity) {
            insertCalled = true;
            return 1;
        }

        @Override
        public int update(E entity) {
            updateCalled = true;
            return 1;
        }

        @Override
        public int delete(E entity) {
            deleteCalled = true;
            return 1;
        }

        @Override
        public E findById(E entity) {
            return findByIdResult;
        }

        @Override
        public List<E> findAll() {
            return findAllResult;
        }

        @Override
        public int checkUpdate(E entity) {
            return 1;
        }

        @Override
        public int checkUpdateList(List<E> entities) {
            return entities.size();
        }
    }

    @TableName("dummy_entity")
    private static class DummyEntity extends BaseEntity {
        @Id
        private Long id;
    }

    private static class NoTableEntity extends BaseEntity {
        @Id
        private Long id;
    }

    @TableName("dummy_entity")
    private static class NoIdEntity extends BaseEntity {
        private Long value;
    }
}
