package de.objektkontor.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.objektkontor.config.annotation.ConfigIdentifier;
import de.objektkontor.config.annotation.ConfigParameter;

public class ConfigInspector {

    public static String dump(Object config) {
        return dump(null, config);
    }

    public static String dump(String prefix, Object config) {
        if (config == null)
            throw new IllegalArgumentException("Config to dump cannot be null");
        StringBuilder buffer = new StringBuilder();
        doDump(prefix, config, buffer);
        return buffer.toString();
    }

    protected static String getParameterKey(String prefix, Field field) {
        String name = field.getAnnotation(ConfigParameter.class).value();
        if (name.equals(ConfigParameter.FIELD_NAME))
            name = field.getName();
        String key = prefix == null ? "" : prefix;
        key += name.length() > 0 ? key.length() > 0 ? "." + name : name : "";
        return key;
    }

    protected static Class<?> getFieldType(Object config, Field field) {
        Object value = getFieldValue(config, field);
        return value == null ? field.getType() : value.getClass();
    }

    protected static Object getFieldValue(Object config, Field field) {
        try {
            return field.get(config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void setFieldValue(Object config, Field field, Object value) {
        try {
            field.set(config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Object newFieldInstance(Field field, Class<?> exactType) {
        Class<?> type = exactType == null ? field.getType() : exactType;
        try {
            return type.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Error creating instance of configuration parameter: " + field);
        }
    }

    protected static List<Field> getConfigParameterFields(Class<?> type) {
        List<Field> result = new LinkedList<>();
        if (type.isArray())
            type = type.getComponentType();
        if (type.isPrimitive())
            return result;
        Class<?> currentClass = type;
        while (currentClass != Object.class) {
            for (Field childField : currentClass.getDeclaredFields())
                if (childField.isAnnotationPresent(ConfigParameter.class))
                    result.add(childField);
            currentClass = currentClass.getSuperclass();
        }
        return result;
    }

    protected static Field getIdentifierField(Class<?> type) {
        Class<?> currentClass = type;
        while (currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields())
                if (field.isAnnotationPresent(ConfigIdentifier.class))
                    return field;
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    protected static boolean hasObserver(Object config) {
        return config instanceof ObservableConfig && ((ObservableConfig) config).hasObserver();
    }

    private static void doDump(String name, Object config, StringBuilder buffer) {
        if (config == null) {
            doValueDump(name, config, buffer);
            return;
        }
        Class<?> type = config.getClass();
        List<Field> parameterFields = getConfigParameterFields(type);
        if (parameterFields.size() > 0) {
            if (type.isArray())
                doConfigArrayDump(name, config, parameterFields, buffer);
            else
                doConfigDump(name, config, parameterFields, buffer);
            return;
        }
        if (type.isArray()) {
            doValueArrayDump(name, config, buffer);
            return;
        }
        doValueDump(name, config, buffer);
    }

    protected static void appendName(StringBuilder buffer, String name) {
        buffer.append("  ").append(name).append(" ");
        if (name.length() < 60) {
            char[] padding = new char[60 - name.length()];
            Arrays.fill(padding, '.');
            buffer.append(padding);
        }
        buffer.append(" ");
    }

    private static void doValueDump(String name, Object value, StringBuilder buffer) {
        appendName(buffer, name);
        buffer.append(String.valueOf(value));
        buffer.append("\n");
    }

    private static void doConfigDump(String name, Object config, List<Field> parameterFields, StringBuilder buffer) {
        for (Field field : parameterFields) {
            field.setAccessible(true);
            Object value = getFieldValue(config, field);
            doDump(getParameterKey(name, field), value, buffer);
        }
    }

    private static void doValueArrayDump(String name, Object array, StringBuilder buffer) {
        appendName(buffer, name + "[]");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i ++) {
            Object value = Array.get(array, i);
            if (i > 0)
                buffer.append(", ");
            buffer.append(String.valueOf(value));
        }
        buffer.append("\n");
    }

    private static void doConfigArrayDump(String name, Object array, List<Field> parameterFields, StringBuilder buffer) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; i ++) {
            Object config = Array.get(array, i);
            if (config == null)
                doValueDump(name + "[" + i + "]", config, buffer);
            else
                doConfigDump(name + "[" + getConfigIndex(config, i) + "]", config, parameterFields, buffer);
        }
    }

    private static String getConfigIndex(Object config, int index) {
        Field identifierField = getIdentifierField(config.getClass());
        if (identifierField == null)
            return Integer.toString(index);
        identifierField.setAccessible(true);
        Object value = getFieldValue(config, identifierField);
        if (value == null)
            return Integer.toString(index);
        return String.valueOf(value);
    }
}