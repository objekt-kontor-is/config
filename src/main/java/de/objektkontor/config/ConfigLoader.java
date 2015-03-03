package de.objektkontor.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.objektkontor.config.annotation.ConfigIdentifier;
import de.objektkontor.config.annotation.ConfigParameter;

public class ConfigLoader {

    private final static Map<Class<?>, Class<?>> primitiveWrappers = createPrimitiveWrappers();

    private final Backend backend;
    private final String bundle;

    public ConfigLoader(Backend backend, String bundle) {
        this.backend = backend;
        this.bundle = bundle;
    }

    private static Map<Class<?>, Class<?>> createPrimitiveWrappers() {
        Map<Class<?>, Class<?>> primitiveWrappers = new HashMap<>();
        primitiveWrappers.put(boolean.class, Boolean.class);
        primitiveWrappers.put(int.class, Integer.class);
        primitiveWrappers.put(long.class, Long.class);
        return primitiveWrappers;
    }

    public <C> C loadConfig(C config) {
        return loadConfig(null, config);
    }

    public <C> C loadConfig(String prefix, C config) {
        Collection<Field> fields = getConfigParameterFields(config.getClass());
        if (fields.size() == 0)
            throw new IllegalArgumentException("No configuration parameters found in class: " + config.getClass());
        loadConfigParameters(prefix, config, fields);
        return config;
    }

    public void loadConfigParameters(String prefix, Object config, Collection<Field> fields) {
        for (Field field : fields) {
            String name = field.getAnnotation(ConfigParameter.class).value();
            if (name.equals(ConfigParameter.FIELD_NAME))
                name = field.getName();
            String key = prefix == null ? "" : prefix;
            key += name.length() > 0 ? key.length() > 0 ? "." + name : name : "";
            field.setAccessible(true);
            try {
                Class<?> type = getFieldType(config, field);
                Collection<Field> childFields = getConfigParameterFields(type);
                if (childFields.size() > 0) {
                    if (type.isArray())
                        loadArrayOfSubConfigs(config, key, field, childFields);
                    else
                        loadSubConfig(config, key, field, childFields);
                    continue;
                }
                if (key.length() == 0)
                    throw new IllegalArgumentException("Empty key for configuration parameter: " + field);
                Object value = loadParameterValue(key, field, field.get(config));
                setParameterValue(config, field, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Class<?> getFieldType(Object config, Field field) throws IllegalAccessException {
        Object value = field.get(config);
        return value == null ? field.getType() : value.getClass();
    }

    private Collection<Field> getConfigParameterFields(Class<?> type) {
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

    private void loadSubConfig(Object config, String key, Field field, Collection<Field> childFields) throws IllegalAccessException, InvocationTargetException {
        Object value = field.get(config);
        Class<?> type = value == null ? field.getType() : value.getClass();
        if (value == null) {
            try {
                value = type.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Error creating instance of configuration parameter: " + field);
            }
            setParameterValue(config, field, value);
        }
        loadConfigParameters(key, value, childFields);
    }

    private void loadArrayOfSubConfigs(Object config, String key, Field field, Collection<Field> childFields) throws IllegalAccessException, InvocationTargetException {
        Object value = field.get(config);
        Class<?> elementType = field.getType().getComponentType();
        Field identifierField = getIdentifierField(elementType);
        Set<String> ids = backend.getSubconfigIds(bundle, key);
        if (value == null) {
            value = Array.newInstance(elementType, ids.size());
            setParameterValue(config, field, value);
        }
        Object[] array = (Object[]) value;
        int index = 0;
        for (String id : ids) {
            Object element;
            try {
                element = elementType.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Error creating instance of configuration parameter: " + field);
            }
            if (identifierField != null) {
                identifierField.setAccessible(true);
                setParameterValue(element, identifierField, id);
            }
            loadConfigParameters(key + "." + id, element, childFields);
            array[index ++] = element;
        }
    }

    private Field getIdentifierField(Class<?> type) {
        Class<?> currentClass = type;
        while (currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields())
                if (field.isAnnotationPresent(ConfigIdentifier.class))
                    return field;
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    private Object loadParameterValue(String key, Field field, Object defaultValue) {
        Class<?> type = field.getType();
        if (type.isArray()) {
            type = type.getComponentType();
            Class<?> loadType = normalizeType(type);
            Object value = backend.getValues(bundle, key, loadType);
            if (value == null)
                return defaultValue;
            if (type.isPrimitive())
                return unboxArray(value, loadType, type);
            return value;
        } else {
            Class<?> loadType = normalizeType(type);
            Object value = backend.getValue(bundle, key, loadType);
            return value == null ? defaultValue : value;
        }
    }

    private void setParameterValue(Object config, Field field, Object value) throws IllegalAccessException, InvocationTargetException {
        Method setter = findSetter(config, field);
        if (setter == null)
            field.set(config, value);
        else
            setter.invoke(config, value);
    }

    private Method findSetter(Object config, Field field) {
        String fieldName = field.getName();
        String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            return config.getClass().getMethod(methodName, field.getType());
        } catch (Exception e) {
            return null;
        }
    }

    private static Class<?> normalizeType(Class<?> type) {
        if (type.isPrimitive())
            return primitiveWrappers.get(type);
        return type;
    }

    private Object unboxArray(Object value, Class<?> boxedType, Class<?> type) {
        if (type == boolean.class) {
            Boolean[] source = (Boolean[]) value;
            boolean[] reault = new boolean[source.length];
            for (int i = 0; i < reault.length; i++)
                reault[i] = source[i];
            return reault;
        }
        if (type == int.class) {
            Integer[] source = (Integer[]) value;
            int[] reault = new int[source.length];
            for (int i = 0; i < reault.length; i++)
                reault[i] = source[i];
            return reault;
        }
        if (type == long.class) {
            Long[] source = (Long[]) value;
            long[] reault = new long[source.length];
            for (int i = 0; i < reault.length; i++)
                reault[i] = source[i];
            return reault;
        }
        throw new IllegalStateException("Unsupported type for unbox array: " + type);
    }
}
