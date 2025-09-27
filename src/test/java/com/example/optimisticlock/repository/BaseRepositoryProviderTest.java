package com.example.optimisticlock.repository;

import com.example.optimisticlock.annotation.TableName;
import com.example.optimisticlock.entity.BaseEntity;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// BaseRepositoryProviderが生成するSQLおよび例外を検証するテスト。
@DisplayName("BaseRepositoryProviderの振る舞い")
class BaseRepositoryProviderTest {

    private final BaseRepositoryProvider provider = new BaseRepositoryProvider();

    @Nested
    @DisplayName("正常系SQL生成")
    class NormalSql {

        @Test
        @DisplayName("INSERT文")
        void insertGeneratesAllColumns() {
            TestEntity entity = createEntity();

            String sql = provider.insert(entity);

            assertTrue(sql.startsWith("INSERT INTO test_entity"));
            assertEquals(new HashSet<>(Arrays.asList(
                "ID",
                "VALUE",
                "ZZCMN_CNAME",
                "ZZCMN_CDATE",
                "ZZCMN_FNAME",
                "ZZCMN_FDATE"
            )), extractColumns(sql));
        }

        @Test
        @DisplayName("UPDATE文")
        void updateUpdatesMutableColumns() {
            TestEntity entity = createEntity();

            String normalized = squash(provider.update(entity));

            assertTrue(normalized.startsWith("UPDATE test_entity"));
            assertTrue(normalized.contains("VALUE = #{value}"));
            assertTrue(normalized.contains("ZZCMN_FNAME = #{zzcmnFname}"));
            assertFalse(normalized.contains("ZZCMN_CNAME"));
            assertTrue(normalized.contains("ZZCMN_FDATE = NOW()"));
            assertFalse(normalized.contains("version"));
        }

        @Test
        @DisplayName("DELETE文")
        void deleteUsesPrimaryKeyOnly() {
            TestEntity entity = createEntity();

            String normalized = squash(provider.delete(entity));

            assertTrue(normalized.startsWith("DELETE FROM test_entity"));
            assertTrue(normalized.contains("ID = #{id}"));
            assertFalse(normalized.contains("version"));
        }

        @Test
        @DisplayName("SELECT(単体)")
        void findByIdUsesPrimaryKey() {
            TestEntity entity = createEntity();

            String normalized = squash(provider.findById(entity));

            assertTrue(normalized.startsWith("SELECT * FROM test_entity"));
            assertTrue(normalized.contains("ID = #{id}"));
        }

        @Test
        @DisplayName("SELECT(全件)")
        void findAllResolvesEntityClassViaContext() {
            String sql = provider.findAll(createProviderContext(TestRepository.class));

            assertEquals("SELECT * FROM test_entity", squash(sql));
        }

        @Test
        @DisplayName("checkUpdateList")
        void checkUpdateListUsesLastUpdateTimestamp() {
            TestEntity entity = createEntity();

            String normalized = squash(provider.checkUpdateList(List.of(entity)));

            assertTrue(normalized.startsWith("SELECT COUNT(1) FROM test_entity"));
            assertTrue(normalized.contains("ID = #{list[0].id}"));
            assertTrue(normalized.contains("ZZCMN_FDATE = #{list[0].zzcmnFdate}"));
        }

        @Test
        @DisplayName("checkUpdateList 空リスト")
        void checkUpdateListReturnsSelectZeroWhenEmpty() {
            String sql = provider.checkUpdateList(Collections.emptyList());
            assertEquals("SELECT 0", sql);
        }

        @Test
        @DisplayName("checkUpdateList null")
        void checkUpdateListReturnsSelectZeroWhenNull() {
            String sql = provider.checkUpdateList(null);
            assertEquals("SELECT 0", sql);
        }
    }

    @Nested
    @DisplayName("異常系")
    class ExceptionalCases {

        @Test
        @DisplayName("@TableNameが欠落_異常系")
        void missingTableName() {
            NoTableEntity entity = new NoTableEntity();
            RepositoryConfigurationException ex = assertThrows(RepositoryConfigurationException.class,
                () -> provider.insert(entity));
            assertTrue(ex.getMessage().contains(NoTableEntity.class.getName()));
        }

        @Test
        @DisplayName("@Idが欠落_異常系")
        void missingId() {
            MissingIdEntity entity = new MissingIdEntity();
            RepositoryConfigurationException ex = assertThrows(RepositoryConfigurationException.class,
                () -> provider.update(entity));
            assertTrue(ex.getMessage().contains(MissingIdEntity.class.getName()));
        }

        @Test
        @DisplayName("エンティティ解決不可_異常系")
        void entityClassUnknown() {
            RepositoryConfigurationException ex = assertThrows(RepositoryConfigurationException.class,
                () -> provider.findAll(createProviderContext(String.class)));
            assertTrue(ex.getMessage().contains(String.class.getName()));
        }
    }

    @Nested
    @DisplayName("getEntityClass")
    class GetEntityClass {

        @Test
        @DisplayName("正常系")
        void resolvesEntityFromMapper() {
            ExposedProvider exposedProvider = new ExposedProvider();
            Class<?> entityClass = exposedProvider.expose(getContext(TestRepository.class));
            assertEquals(TestEntity.class, entityClass);
        }

        @Test
        @DisplayName("解決不可_異常系")
        void throwsWhenEntityUnknown() {
            ExposedProvider exposedProvider = new ExposedProvider();
            RepositoryConfigurationException ex = assertThrows(RepositoryConfigurationException.class,
                () -> exposedProvider.expose(getContext(String.class)));
            assertTrue(ex.getMessage().contains(String.class.getName()));
        }

        private ProviderContext getContext(Class<?> type) {
            return createProviderContext(type);
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

    private interface TestRepository extends BaseRepository<TestEntity> {}

    private static class NoTableEntity extends BaseEntity {
        @Id
        private Long id;
    }

    @TableName("test_entity")
    private static class MissingIdEntity extends BaseEntity {
        private String value;
    }

    private ProviderContext createProviderContext(Class<?> mapperType) {
        try {
            Constructor<ProviderContext> constructor = ProviderContext.class
                .getDeclaredConstructor(Class.class, Method.class, String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(mapperType, null, null);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate ProviderContext", ex);
        }
    }

    private static class ExposedProvider extends BaseRepositoryProvider {
        Class<?> expose(ProviderContext context) {
            return getEntityClass(context);
        }
    }
}
