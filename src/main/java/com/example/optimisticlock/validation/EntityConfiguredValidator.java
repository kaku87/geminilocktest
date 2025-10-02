package com.example.optimisticlock.validation;

import com.example.optimisticlock.annotation.TableName;
import com.example.optimisticlock.entity.BaseEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import javax.persistence.Id;
import java.lang.reflect.Field;

public class EntityConfiguredValidator implements ConstraintValidator<EntityConfigured, BaseEntity> {

    @Override
    public boolean isValid(BaseEntity value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Class<?> entityClass = value.getClass();
        boolean hasTableName = entityClass.isAnnotationPresent(TableName.class);
        boolean hasIdField = hasIdField(entityClass);

        if (hasTableName && hasIdField) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        if (!hasTableName) {
            context.buildConstraintViolationWithTemplate(
                    "Entity " + entityClass.getName() + " must be annotated with @TableName")
                .addConstraintViolation();
        }
        if (!hasIdField) {
            context.buildConstraintViolationWithTemplate(
                    "Entity " + entityClass.getName() + " must declare at least one field annotated with @Id")
                .addConstraintViolation();
        }
        return false;
    }

    private boolean hasIdField(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return true;
                }
            }
            current = current.getSuperclass();
        }
        return false;
    }
}
