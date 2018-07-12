package de.objektkontor.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

public class ConfigDuplicator extends ConfigInspector {

	/**
	 * Deep copy values from source into target config object.
	 *
	 * @param source
	 * @param target
	 */
	public static void copyConfig(Object source, Object target) {
		copyConfig(source, target, false);
	}

	/**
	 * Deep copy values from source into target config object.
	 *
	 * @param source
	 * @param target
	 * @param skipDefaults
	 */
	public static void copyConfig(Object source, Object target, boolean skipDefaults) {
		if (source == null || target == null)
			throw new IllegalArgumentException("source and/or target cannot be null");
		Class<?> type = source.getClass();
		List<Field> parameterFields = getConfigParameterFields(type);
		if (parameterFields.size() > 0) {
			copyConfig(source, target, parameterFields, skipDefaults);
			return;
		}
		throw new IllegalArgumentException("cannot copy objects of type: " + type);
	}

	/**
	 * Creates deep clone of specified source config.
	 *
	 * @param source
	 * @return
	 */
	public static <C> C cloneConfig(C source) {
		if (source == null)
			throw new IllegalArgumentException("source cannot be null");
		C target = newInstanceOfSameType(source);
		copyConfig(source, target);
		return target;
	}

	/**
	 * Diplicates specified template and completes result with non default
	 * values from specified config Non default values determinated by comparing
	 * config values against values from new config instance, created internally
	 * by calling default constructor.
	 *
	 * @param config
	 * @param template
	 * @return
	 */
	public static <C> C completeConfig(C config, C template) {
		if (config == null || template == null)
			throw new IllegalArgumentException("config and/or template cannot be null");
		C target = cloneConfig(template);
		copyConfig(config, target, true);
		return target;
	}

	private static void copyConfig(Object source, Object target, List<Field> parameterFields, boolean skipDefaults) {
		Field identifierField = ConfigInspector.getIdentifierField(source.getClass());
		if (identifierField != null) {
			identifierField.setAccessible(true);
			setFieldValue(target, identifierField, getFieldValue(source, identifierField));
		}
		Object defaultSource = skipDefaults ? newInstanceOfSameType(source) : null;
		for (Field field : parameterFields) {
			field.setAccessible(true);
			Class<?> type = getFieldType(source, field);
			List<Field> childFields = ConfigInspector.getConfigParameterFields(type);
			if (childFields.size() > 0) {
				if (type.isArray()) {
					copyArrayOfSubConfigs(source, target, field, childFields, skipDefaults);
				} else {
					copySubConfig(source, target, field, childFields, skipDefaults);
				}
				continue;
			}
			if (type.isArray()) {
				copyFieldArrayValue(source, target, field, skipDefaults);
			} else {
				Object sourceValue = getFieldValue(source, field);
				if (skipDefaults &&
						(sourceValue == null ||
								sourceValue.equals(getFieldValue(defaultSource, field)))) {
					continue;
				}
				setFieldValue(target, field, sourceValue);
			}
		}
	}

	private static void copyFieldArrayValue(Object source, Object target, Field field, boolean skipDefaults) {
		Object sourceArray = getFieldValue(source, field);
		if (sourceArray == null) {
			if (!skipDefaults) {
				setFieldValue(target, field, null);
			}
		} else {
			Object targetArray = getFieldValue(target, field);
			Class<?> elementType = sourceArray.getClass().getComponentType();
			int length = Array.getLength(sourceArray);
			if (targetArray == null || Array.getLength(targetArray) != length) {
				targetArray = Array.newInstance(elementType, length);
			}
			if (length > 0) {
				System.arraycopy(sourceArray, 0, targetArray, 0, length);
			}
			setFieldValue(target, field, targetArray);
		}
	}

	private static void copySubConfig(Object source, Object target, Field field, List<Field> childFields, boolean skipDefaults) {
		Object sourceConfig = getFieldValue(source, field);
		if (sourceConfig == null) {
			if (!skipDefaults) {
				setFieldValue(target, field, null);
			}
		} else {
			Object targetConfig = getFieldValue(target, field);
			if (targetConfig == null) {
				Class<?> type = sourceConfig.getClass();
				targetConfig = newFieldInstance(field, type);
				setFieldValue(target, field, targetConfig);
			}
			copyConfig(sourceConfig, targetConfig, childFields, skipDefaults);
		}
	}

	private static void copyArrayOfSubConfigs(Object source, Object target, Field field, List<Field> childFields, boolean skipDefaults) {
		Object sourceArray = getFieldValue(source, field);
		if (sourceArray == null) {
			if (!skipDefaults) {
				setFieldValue(target, field, null);
			}
		} else {
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
				copyConfig(sourceConfig, targetConfig, childFields, skipDefaults);
			}
		}
	}
}
