package de.objektkontor.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

public class ConfigDuplicator extends ConfigInspector {

    public static void copyConfig(Object source, Object target) {
        if (source == null || target == null)
            throw new IllegalArgumentException("source and/or target cannot be null");
        Class<?> type = source.getClass();
        List<Field> parameterFields = getConfigParameterFields(type);
        if (parameterFields.size() > 0) {
            copyConfig(source, target, parameterFields);
            return;
        }
        throw new IllegalArgumentException("cannot copy objects of type: " + type);
    }

    @SuppressWarnings("unchecked")
    public static <C> C cloneConfig(C source) {
        if (source == null)
            throw new IllegalArgumentException("source cannot be null");
        Class<?> type = source.getClass();
        try {
            Object target = type.newInstance();
            copyConfig(source, target);
            return (C) target;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Error creating instance of config: " + type, e);
        }
    }

    private static void copyConfig(Object source, Object target, List<Field> parameterFields) {
        Field identifierField = ConfigInspector.getIdentifierField(source.getClass());
        if (identifierField != null) {
            identifierField.setAccessible(true);
            copyFieldValue(source, target, identifierField);
        }
        for (Field field : parameterFields) {
            field.setAccessible(true);
            Class<?> type = getFieldType(source, field);
            List<Field> childFields = ConfigInspector.getConfigParameterFields(type);
            if (childFields.size() > 0) {
                if (type.isArray())
                    copyArrayOfSubConfigs(source, target, field, childFields);
                else
                    copySubConfig(source, target, field, childFields);
                continue;
            }
            if (type.isArray())
                copyFieldArrayValue(source, target, field);
            else
                copyFieldValue(source, target, field);
        }
    }

    private static void copyFieldValue(Object source, Object target, Field field) {
        Object value = getFieldValue(source, field);
        setFieldValue(target, field, value);
    }

    private static void copyFieldArrayValue(Object source, Object target, Field field) {
        Object sourceArray = getFieldValue(source, field);
        if (sourceArray == null)
            setFieldValue(target, field, null);
        else {
            Object targetArray = getFieldValue(target, field);
            Class<?> elementType = sourceArray.getClass().getComponentType();
            int length = Array.getLength(sourceArray);
            if (targetArray == null || Array.getLength(targetArray) != length)
                targetArray = Array.newInstance(elementType, length);
            if (length > 0)
                System.arraycopy(sourceArray, 0, targetArray, 0, length);
            setFieldValue(target, field, targetArray);
        }
    }

    private static void copySubConfig(Object source, Object target, Field field, List<Field> childFields) {
        Object sourceConfig = getFieldValue(source, field);
        if (sourceConfig == null)
            setFieldValue(target, field, null);
        else {
            Object targetConfig = getFieldValue(target, field);
            if (targetConfig == null) {
                Class<?> type = sourceConfig.getClass();
                targetConfig = newFieldInstance(field, type);
                setFieldValue(target, field, targetConfig);
            }
            copyConfig(sourceConfig, targetConfig, childFields);
        }
    }

    private static void copyArrayOfSubConfigs(Object source, Object target, Field field, List<Field> childFields) {
        Object sourceArray = getFieldValue(source, field);
        if (sourceArray == null)
            setFieldValue(target, field, null);
        else {
            Object targetArray = getFieldValue(target, field);
            Class<?> elementType = sourceArray.getClass().getComponentType();
            int length = Array.getLength(sourceArray);
            if (targetArray == null || Array.getLength(targetArray) != length) {
                targetArray = Array.newInstance(elementType, length);
                setFieldValue(target, field, targetArray);
            }
            for (int index = 0; index < length; index++) {
                Object sourceConfig = Array.get(sourceArray, index);
                Object targetConfig = Array.get(targetArray, index);
                if (targetConfig == null) {
                    targetConfig = newFieldInstance(field, elementType);
                    Array.set(targetArray, index, targetConfig);
                }
                copyConfig(sourceConfig, targetConfig, childFields);
            }
        }
    }
}
