package com.example.optimisticlock.repository;

import com.example.optimisticlock.annotation.TableName;
import com.example.optimisticlock.entity.BaseEntity;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// BaseRepositoryProviderが生成するSQLの妥当性を検証するテスト。
@DisplayName("BaseRepositoryProviderのSQL生成")
class BaseRepositoryProviderTest {

    private final BaseRepositoryProvider provider = new BaseRepositoryProvider();

    @Nested
    @DisplayName("insert")
    class Insert {

        @Test
        @DisplayName("正常系")
        void normal() {
            TestEntity entity = createEntity();

            String sql = provider.insert(entity);

            assertTrue(sql.startsWith("INSERT INTO test_entity"));
            assertEquals(new HashSet<>(Arrays.asList(
                "id",
                "value",
                "zzcmn_cname",
                "zzcmn_cdate",
                "zzcmn_fname",
                "zzcmn_fdate"
            )), extractColumns(sql));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("正常系")
        void normal() {
            TestEntity entity = createEntity();

            String sql = provider.update(entity);
            String normalized = squash(sql);

            assertTrue(normalized.startsWith("UPDATE test_entity"), normalized);
            assertFalse(normalized.contains("SET id ="), normalized);
            assertTrue(normalized.contains("zzcmn_fdate = NOW()"), normalized);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("正常系")
        void normal() {
            TestEntity entity = createEntity();

            String sql = provider.delete(entity);
            String normalized = squash(sql);

            assertTrue(normalized.startsWith("DELETE FROM test_entity"), normalized);
            assertTrue(normalized.contains("id = #{id}"), normalized);
            assertFalse(normalized.contains("version"), normalized);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("正常系")
        void normal() {
            TestEntity entity = createEntity();

            String sql = provider.findById(entity);
            String normalized = squash(sql);

            assertTrue(normalized.startsWith("SELECT * FROM test_entity"), normalized);
            assertTrue(normalized.contains("id = #{id}"), normalized);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("正常系")
        void normal() {
            String sql = provider.findAll(ProviderContextFactory.create(DummyRepository.class));

            assertEquals("SELECT * FROM test_entity", squash(sql));
        }
    }

    @Nested
    @DisplayName("checkUpdateList")
    class CheckUpdateList {

        @Test
        @DisplayName("正常系")
        void normal() {
            TestEntity entity = createEntity();
            String sql = provider.checkUpdateList(List.of(entity));
            String normalized = squash(sql);

            assertTrue(normalized.startsWith("SELECT COUNT(1) FROM test_entity"));
            assertTrue(normalized.contains("id = #{list[0].id}"));
            assertTrue(normalized.contains("zzcmn_fdate = #{list[0].zzcmnFdate}"));
        }
    }

    private TestEntity createEntity() {
        TestEntity entity = new TestEntity();
        entity.setId(1L);
        entity.setValue("value");
        return entity;
    }

    private Set<String> extractColumns(String insertSql) {
        int start = insertSql.indexOf('(');
        int end = insertSql.indexOf(')');
        String columnsPart = insertSql.substring(start + 1, end);
        return new HashSet<>(Arrays.asList(columnsPart.replace(" ", "").split(",")));
    }

    private String squash(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    @TableName("test_entity")
    private static class TestEntity extends BaseEntity {
        @Id
        private Long id;
        private String value;

        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        String getValue() {
            return value;
        }

        void setValue(String value) {
            this.value = value;
        }
    }

    private interface DummyRepository extends BaseRepository<TestEntity> {}

    /**
     * MyBatisのProviderContextはパッケージプライベートなコンストラクターしか持たないため、
     * テストではリフレクションを介して生成するヘルパーを用意する。
     */
    private static final class ProviderContextFactory {
        private static final Constructor<ProviderContext> CONSTRUCTOR;

        static {
            try {
                CONSTRUCTOR = ProviderContext.class
                    .getDeclaredConstructor(Class.class, java.lang.reflect.Method.class, String.class);
                CONSTRUCTOR.setAccessible(true);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to prepare ProviderContext constructor", ex);
            }
        }

        private ProviderContextFactory() {
        }

        static ProviderContext create(Class<?> mapperType) {
            try {
                return CONSTRUCTOR.newInstance(mapperType, null, null);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to instantiate ProviderContext", ex);
            }
        }
    }
}
