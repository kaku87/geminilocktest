package com.example.optimisticlock.repository;

import com.example.optimisticlock.annotation.TableName;
import com.example.optimisticlock.entity.BaseEntity;
import javax.persistence.Id;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

public class BaseRepositoryProvider implements ProviderMethodResolver {

    public String insert(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
        }
        String tableName = tableNameAnnotation.value();

        return new SQL() {{
            INSERT_INTO(tableName);
            for (Field field : getAllFields(entityClass)) {
                VALUES(camelToSnake(field.getName()), "#{" + field.getName() + "}");
            }
        }}.toString();
    }

    public String update(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
        }
        String tableName = tableNameAnnotation.value();

        List<Field> idFields = findIdFields(entityClass);

        return new SQL() {{
            UPDATE(tableName);

            String setClauses = Arrays.stream(getAllFields(entityClass))
                .filter(field -> !field.isAnnotationPresent(Id.class) && !field.getName().equals("zzcmnFdate") && !field.getName().equals("version"))
                .map(field -> camelToSnake(field.getName()) + " = #{" + field.getName() + "}")
                .collect(Collectors.joining(", "));

            SET(setClauses);
            SET("version = version + 1");
            SET("zzcmn_fdate = NOW()");

            for (Field idField : idFields) {
                String idColumnName = camelToSnake(idField.getName());
                String idFieldName = idField.getName();
                WHERE(idColumnName + " = #{" + idFieldName + "}");
            }

            WHERE("version = #{version}");
        }}.toString();
    }

    public String delete(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
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

            WHERE("version = #{version}");
        }}.toString();
    }

    public String findById(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
        }
        String tableName = tableNameAnnotation.value();

        SQL sql = new SQL().SELECT("*").FROM(tableName);
        List<Field> idFields = findIdFields(entityClass);
        for (Field idField : idFields) {
            sql.WHERE(camelToSnake(idField.getName()) + " = #{" + idField.getName() + "}");
        }
        return sql.toString();
    }

    public String findAll(ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
        }
        String tableName = tableNameAnnotation.value();

        return new SQL() {{
            SELECT("*");
            FROM(tableName);
        }}.toString();
    }


    private List<Field> findIdFields(Class<?> clazz) {
        List<Field> idFields = new ArrayList<>();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(Id.class)) {
                idFields.add(field);
            }
        }
        if (idFields.isEmpty()) {
            throw new RuntimeException("No @Id annotation found in class " + clazz.getName() + " or its superclasses.");
        }
        return idFields;
    }

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    private static String camelToSnake(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public String checkUpdate(BaseEntity entity) {
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
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

            WHERE("version = #{version}");
        }}.toString();
    }

    public String checkUpdateList(List<BaseEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return "SELECT 0";
        }

        BaseEntity entity = entities.get(0);
        Class<?> entityClass = entity.getClass();
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            throw new RuntimeException("Entity class " + entityClass.getName() + " must be annotated with @TableName");
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
            conditions.add("version = #{list[" + i + "].version}");
            whereClause.append(String.join(" AND ", conditions));
            whereClause.append(")");
        }
        whereClause.append(")");

        sql.append(whereClause);

        return sql.toString();
    }

    private Class<?> getEntityClass(ProviderContext context) {
        Type[] genericInterfaces = context.getMapperType().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (parameterizedType.getRawType().equals(BaseRepository.class)) {
                    Type type = parameterizedType.getActualTypeArguments()[0];
                    if (type instanceof Class) {
                        return (Class<?>) type;
                    }
                }
            }
        }
        // Fallback for searching superclass hierarchy
        Class<?> mapperClass = context.getMapperType();
        while(mapperClass != null) {
            genericInterfaces = mapperClass.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    if (parameterizedType.getRawType().equals(BaseRepository.class)) {
                        Type type = parameterizedType.getActualTypeArguments()[0];
                        if (type instanceof Class) {
                            return (Class<?>) type;
                        }
                    }
                }
            }
            mapperClass = mapperClass.getSuperclass();
        }


        throw new RuntimeException("Could not determine entity class for " + context.getMapperType().getName());
    }
}
