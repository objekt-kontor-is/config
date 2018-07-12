package de.objektkontor.config;

import static java.lang.String.format;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.objektkontor.config.annotation.ConfigIdentifier;
import de.objektkontor.config.annotation.ConfigParameter;

public class ConfigInspector {

	private static final int DUMP_PARAMETER_NAME_LENGTH = 60;
	private static final int DUMP_PARAMETER_VALUE_LENGTH = 30;

	public static String dump(Object config) {
		return dump(null, config, false);
	}

	public static String dump(Object config, boolean skipNulls) {
		return dump(null, config, skipNulls);
	}

	public static String dump(String prefix, Object config, boolean skipNulls) {
		if (config == null)
			throw new IllegalArgumentException("Config to dump cannot be null");
		StringBuilder buffer = new StringBuilder();
		doDump(prefix, config, null, skipNulls, buffer);
		return buffer.toString();
	}

	protected static String getParameterKey(String prefix, Field field) {
		String name = field.getAnnotation(ConfigParameter.class).value();
		if (name.equals(ConfigParameter.FIELD_NAME)) {
			name = field.getName();
		}
		String key = prefix == null ? "" : prefix;
		key += name.length() > 0 ? key.length() > 0 ? "." + name : name : "";
		return key;
	}

	protected static String getParameterDescription(Field field) {
		String description = field.getAnnotation(ConfigParameter.class).description();
		if (ConfigParameter.NO_DESCRIPTION.equals(description))
			return null;
		return description;
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

    @SuppressWarnings("unchecked")
    protected static <C> C newInstanceOfSameType(C source) {
        Class<?> type = source.getClass();
        try {
            return (C) type.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Error creating instance of type: " + type, e);
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
		if (type.isArray()) {
			type = type.getComponentType();
		}
		if (type.isPrimitive())
			return result;
		Class<?> currentClass = type;
		while (currentClass != Object.class) {
			for (Field childField : currentClass.getDeclaredFields())
				if (childField.isAnnotationPresent(ConfigParameter.class)) {
					result.add(childField);
				}
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

	private static void doDump(String name, Object config, String description, boolean skipNulls, StringBuilder buffer) {
		if (config == null) {
			doValueDump(name, config, description, skipNulls, buffer);
			return;
		}
		Class<?> type = config.getClass();
		List<Field> parameterFields = getConfigParameterFields(type);
		if (parameterFields.size() > 0) {
			if (type.isArray()) {
				doConfigArrayDump(name, config, parameterFields, skipNulls, buffer);
			} else {
				doConfigDump(name, config, parameterFields, skipNulls, buffer);
			}
			return;
		}
		if (type.isArray()) {
			doValueArrayDump(name, config, description, skipNulls, buffer);
			return;
		}
		doValueDump(name, config, description, skipNulls, buffer);
	}

	protected static void appendName(StringBuilder buffer, String name) {
		buffer.append("  ").append(name).append(" ");
		if (name.length() < DUMP_PARAMETER_NAME_LENGTH) {
			char[] padding = new char[DUMP_PARAMETER_NAME_LENGTH - name.length()];
			Arrays.fill(padding, '.');
			buffer.append(padding);
		}
		buffer.append(" ");
	}

	private static void doValueDump(String name, Object value, String description, boolean skipNulls, StringBuilder buffer) {
		if (value != null || !skipNulls) {
			appendName(buffer, name);
			buffer.append(format("%-" + DUMP_PARAMETER_VALUE_LENGTH + "s", String.valueOf(value)));
			if (description != null) {
				buffer.append(" - ").append(description);
			}
			buffer.append("\n");
		}
	}

	private static void doConfigDump(String name, Object config, List<Field> parameterFields, boolean skipNulls, StringBuilder buffer) {
		for (Field field : parameterFields) {
			field.setAccessible(true);
			Object value = getFieldValue(config, field);
			doDump(getParameterKey(name, field), value, getParameterDescription(field), skipNulls, buffer);
		}
	}

	private static void doValueArrayDump(String name, Object array, String description, boolean skipNulls, StringBuilder buffer) {
		StringBuilder valuesBuffer = new StringBuilder();
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++) {
			Object value = Array.get(array, i);
			if (i > 0) {
				valuesBuffer.append(", ");
			}
			valuesBuffer.append(String.valueOf(value));
		}
		doValueDump(name + "[]", valuesBuffer, description, skipNulls, buffer);
	}

	private static void doConfigArrayDump(String name, Object array, List<Field> parameterFields, boolean skipNulls, StringBuilder buffer) {
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++) {
			Object config = Array.get(array, i);
			if (config == null) {
				doValueDump(name + "[" + i + "]", config, null, skipNulls, buffer);
			} else {
				doConfigDump(name + "[" + getConfigIndex(config, i) + "]", config, parameterFields, skipNulls, buffer);
			}
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
