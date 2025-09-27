package com.example.optimisticlock.repository;

import com.example.optimisticlock.annotation.TableName;
import com.example.optimisticlock.entity.BaseEntity;
import javax.persistence.Id;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

// MyBatisの@*Providerから呼び出される共通SQLビルダー。
public class BaseRepositoryProvider implements ProviderMethodResolver {

    /**
     * エンティティ全フィールドをINSERTするSQLを生成する。
     * @param entity 永続化対象のエンティティ
     * @return INSERT文
     */
    public String insert(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        return new SQL() {{
            INSERT_INTO(tableName);
            for (Field field : getAllFields(entityClass)) {
                VALUES(camelToSnake(field.getName()), "#{" + field.getName() + "}");
            }
        }}.toString();
    }

    /**
     * 更新者情報とタイムスタンプを反映しながら指定エンティティを更新するSQLを生成する。
     * @param entity 更新対象のエンティティ
     * @return UPDATE文
     */
    public String update(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        List<Field> idFields = findIdFields(entityClass);

        return new SQL() {{
            UPDATE(tableName);

            String setClauses = Arrays.stream(getAllFields(entityClass))
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .filter(field -> !field.getName().equals("zzcmnFdate"))
                .filter(field -> !field.getName().equals("zzcmnCname"))
                .filter(field -> !field.getName().equals("zzcmnCdate"))
                .map(field -> camelToSnake(field.getName()) + " = #{" + field.getName() + "}")
                .collect(Collectors.joining(", "));

            SET(setClauses);
            SET("ZZCMN_FDATE = NOW()");

            for (Field idField : idFields) {
                String idColumnName = camelToSnake(idField.getName());
                String idFieldName = idField.getName();
                WHERE(idColumnName + " = #{" + idFieldName + "}");
            }
        }}.toString();
    }

    /**
     * 主キーに基づいてレコードを削除するSQLを生成する。
     * @param entity 削除対象のエンティティ
     * @return DELETE文
     */
    public String delete(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        List<Field> idFields = findIdFields(entityClass);

        return new SQL() {{
            DELETE_FROM(tableName);

            for (Field idField : idFields) {
                String idColumnName = camelToSnake(idField.getName());
                String idFieldName = idField.getName();
                WHERE(idColumnName + " = #{" + idFieldName + "}");
            }
        }}.toString();
    }

    /**
     * 主キー条件で単一レコードを検索するSQLを生成する。
     * @param entity 検索条件を保持するエンティティ
     * @return SELECT文
     */
    public String findById(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        SQL sql = new SQL().SELECT("*").FROM(tableName);
        List<Field> idFields = findIdFields(entityClass);
        for (Field idField : idFields) {
            sql.WHERE(camelToSnake(idField.getName()) + " = #{" + idField.getName() + "}");
        }
        return sql.toString();
    }

    /**
     * マッパー型情報からエンティティクラスを特定し、全件取得のSQLを作成する。
     * @param context MyBatisが提供するプロバイダーコンテキスト
     * @return SELECT文
     */
    public String findAll(ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        return new SQL() {{
            SELECT("*");
            FROM(tableName);
        }}.toString();
    }


    /**
     * 継承階層も含めて@Idが付与されたフィールドを収集する。
     * @param clazz 解析対象のクラス
     * @return @Idフィールドのリスト
     */
    private List<Field> findIdFields(Class<?> clazz) {
        List<Field> idFields = new ArrayList<>();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(Id.class)) {
                idFields.add(field);
            }
        }
        if (idFields.isEmpty()) {
            throw new RepositoryConfigurationException("repository.idMissing", clazz.getName());
        }
        return idFields;
    }

    /**
     * 指定クラスとスーパークラスから全フィールドリストを取得する。
     * @param clazz 解析対象のクラス
     * @return フィールド配列
     */
    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !field.isSynthetic())
                .filter(field -> !field.getName().startsWith("$jacoco"))
                .forEach(fields::add);
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * camelCaseのプロパティ名をスネークケースのカラム名へ変換する。
     * @param str プロパティ名
     * @return スネークケースへ変換した文字列
     */
    private static String camelToSnake(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }

    /**
     * 単一エンティティの楽観ロックチェック用SELECTを生成する。
     * @param entity 楽観ロック検証対象のエンティティ
     * @return SELECT文
     */
    public String checkUpdate(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        List<Field> idFields = findIdFields(entityClass);

        return new SQL() {{
            SELECT("count(1)");
            FROM(tableName);

            for (Field idField : idFields) {
                String idColumnName = camelToSnake(idField.getName());
                String idFieldName = idField.getName();
                WHERE(idColumnName + " = #{" + idFieldName + "}");
            }

            WHERE("ZZCMN_FDATE = #{zzcmnFdate}");
        }}.toString();
    }

    /**
     * 複数エンティティの最終更新日時をまとめて確認するためのSELECTを生成する。
     * @param entities 楽観ロック検証対象エンティティのリスト
     * @return SELECT文
     */
    public String checkUpdateList(List<BaseEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return "SELECT 0";
        }

        BaseEntity entity = entities.get(0);
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RepositoryConfigurationException("repository.tableNameMissing", entityClass.getName());
        }
        String tableName = tableNameAnnotation.value();

        List<Field> idFields = findIdFields(entityClass);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) FROM ").append(tableName).append(" WHERE ");

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("(");

        for (int i = 0; i < entities.size(); i++) {
            if (i > 0) {
                whereClause.append(" OR ");
            }
            whereClause.append("(");
            List<String> conditions = new ArrayList<>();
            for (Field idField : idFields) {
                conditions.add(camelToSnake(idField.getName()) + " = #{list[" + i + "]." + idField.getName() + "}");
            }
            conditions.add("ZZCMN_FDATE = #{list[" + i + "].zzcmnFdate}");
            whereClause.append(String.join(" AND ", conditions));
            whereClause.append(")");
        }
        whereClause.append(")");

        sql.append(whereClause);

        return sql.toString();
    }

    /**
     * BaseRepository<T>の型引数として宣言されたエンティティ型を取得する。
     * @param context MyBatisが提供するプロバイダーコンテキスト
     * @return エンティティクラス
     */
    protected Class<?> getEntityClass(ProviderContext context) {
        // Every repository is expected to implement BaseRepository<T>; walk the type hierarchy until we find that T.
        for (Class<?> type = context.getMapperType(); type != null; type = type.getSuperclass()) {
            for (Type genericInterface : type.getGenericInterfaces()) {
                Class<?> entityClass = resolveEntityClass(genericInterface);
                if (entityClass != null) {
                    return entityClass;
                }
            }
        }
        throw new RepositoryConfigurationException("repository.entityClassUnknown", context.getMapperType().getName());
    }

    /**
     * BaseRepository<T>型からエンティティクラスを抽出する補助メソッド。
     * @param candidate 解析対象の型情報
     * @return 抽出されたエンティティクラス、存在しない場合はnull
     */
    private Class<?> resolveEntityClass(Type candidate) {
        if (candidate instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) candidate;
            if (BaseRepository.class.equals(parameterizedType.getRawType())) {
                Type actualType = parameterizedType.getActualTypeArguments()[0];
                if (actualType instanceof Class) {
                    return (Class<?>) actualType;
                }
            }
        }
        return null;
    }
}
