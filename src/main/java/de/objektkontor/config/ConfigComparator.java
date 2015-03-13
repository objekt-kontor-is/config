package de.objektkontor.config;

import static java.lang.Math.max;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

public class ConfigComparator extends ConfigInspector {

    public static int SHALLOW = 1;
    public static int DEEP = -1;

    private int depth;
    private final int maxDepth;

    private ConfigComparator(int depth) {
        this.depth = depth;
        maxDepth = depth;
    }

    public static boolean equals(Object first, Object second) {
        return new ConfigComparator(SHALLOW).doEquals(first, second, true);
    }

    public static boolean equals(Object first, Object second, int depth) {
        return new ConfigComparator(depth).doEquals(first, second, true);
    }

    public static boolean equals(Object first, Object second, int depth, boolean ignoreObservable) {
        return new ConfigComparator(depth).doEquals(first, second, ignoreObservable);
    }

    public static boolean deepEquals(Object first, Object second) {
        return new ConfigComparator(DEEP).doEquals(first, second, true);
    }

    public static boolean deepEquals(Object first, Object second, boolean ignoreObservable) {
        return new ConfigComparator(DEEP).doEquals(first, second, ignoreObservable);
    }

    public static String diff(Object first, Object second) {
        return diff(null, first, second);
    }

    public static String diff(String prefix, Object first, Object second) {
        if (first == null || second == null)
            throw new IllegalArgumentException("Config to diff cannot be null");
        StringBuilder buffer = new StringBuilder();
        doDiff(prefix, first, second, buffer);
        return buffer.toString();
    }

    private boolean doEquals(Object first, Object second, boolean ignoreObservable) {
        return doEquals(first, second, (Class<?>) null, ignoreObservable);
    }

    private boolean doEquals(Object first, Object second, Class<?> ownerType, boolean ignoreObservable) {
        if (first == null)
            return second == null;
        if (second == null)
            return false;
        Class<?> type = first.getClass();
        if (type.isArray())
            return doArraysEquals(first, second, ownerType);
        List<Field> parameterFields = getConfigParameterFields(type);
        if (parameterFields.size() > 0) {
            if (depth == 0)
                return true;
            if (ignoreObservable && !atRootDepth() && hasObserver(first))
                return true;
            depth--;
            return doEquals(first, second, parameterFields, ignoreObservable);
        }
        return first.equals(second);
    }

    private boolean doEquals(Object first, Object second, List<Field> parameterFields, boolean ignoreObservable) {
        Field identifierField = getIdentifierField(first.getClass());
        if (identifierField != null)
            if (!doEquals(first, second, identifierField, false))
                return false;
        for (Field field : parameterFields)
            if (!doEquals(first, second, field, ignoreObservable))
                return false;
        return true;
    }

    private boolean doEquals(Object first, Object second, Field field, boolean ignoreObservable) {
        field.setAccessible(true);
        Object firstValue = getFieldValue(first, field);
        Object secondValue = getFieldValue(second, field);
        return doEquals(firstValue, secondValue, field.getDeclaringClass(), ignoreObservable);
    }

    private boolean doArraysEquals(Object first, Object second, Class<?> ownerType) {
        int firstLength = Array.getLength(first);
        int secondLength = Array.getLength(second);
        if (firstLength != secondLength)
            return false;
        for (int i = 0; i < firstLength; i++) {
            Object firstValue = Array.get(first, i);
            Object secondValue = Array.get(second, i);
            if (!ownerIsArray(ownerType))
                depth++;
            if (!doEquals(firstValue, secondValue, first.getClass(), false))
                return false;
        }
        return true;
    }

    private boolean atRootDepth() {
        return depth == DEEP || depth == maxDepth;
    }

    private boolean ownerIsArray(Class<?> ownerType) {
        return ownerType == null || ownerType.isArray();
    }

    private static void doDiff(String name, Object first, Object second, StringBuilder buffer) {
        if (deepEquals(first, second, false))
            return;
        Class<?> type = first != null ? first.getClass() : second.getClass();
        if (type.isArray()) {
            doArraysDiff(name, first, second, buffer);
            return;
        }
        List<Field> parameterFields = getConfigParameterFields(type);
        if (parameterFields.size() > 0) {
            doConfigsDiff(name, first, second, parameterFields, buffer);
            return;
        }
        doValuesDiff(name, first, second, buffer);
    }

    private static void doValuesDiff(String name, Object first, Object second, StringBuilder buffer) {
        appendName(buffer, name);
        buffer.append(String.valueOf(first));
        buffer.append(" <=> ");
        buffer.append(String.valueOf(second));
        buffer.append("\n");
    }

    private static void doConfigsDiff(String name, Object first, Object second, List<Field> parameterFields, StringBuilder buffer) {
        for (Field field : parameterFields) {
            field.setAccessible(true);
            Object firstValue = first == null ? null : getFieldValue(first, field);
            Object secondValue = second == null ? null : getFieldValue(second, field);
            doDiff(getParameterKey(name, field), firstValue, secondValue, buffer);
        }
    }

    private static void doArraysDiff(String name, Object first, Object second, StringBuilder buffer) {
        int firstLength = first == null ? 0 : Array.getLength(first);
        int secondLength = second == null ? 0 : Array.getLength(second);
        for (int i = 0; i < max(firstLength, secondLength); i++) {
            Object firstValue = i < firstLength ? Array.get(first, i) : null;
            Object secondValue = i < secondLength ? Array.get(second, i) : null;
            doDiff(name + "[" + i + "]", firstValue, secondValue, buffer);
        }
    }
}
