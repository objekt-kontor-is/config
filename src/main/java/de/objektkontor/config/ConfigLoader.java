package de.objektkontor.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigLoader extends ConfigInspector implements BundleObserver {

	private final static Logger log = LoggerFactory.getLogger(ConfigLoader.class);

	private final static Map<Class<?>, Class<?>> primitiveWrappers = createPrimitiveWrappers();

	private final ConfigBackend configBackend;

	private final ConfigTracker<TrackingData> configTracker;

	public ConfigLoader(ConfigBackend configBackend) {
		this(configBackend, false);
	}

	public ConfigLoader(ConfigBackend configBackend, boolean enableTracking) {
		this.configBackend = configBackend;
		if (enableTracking) {
			configTracker = new ConfigTracker<>();
			configBackend.addObserver(this);
		} else {
			configTracker = null;
		}
	}

	public <C> C loadConfig(C config) {
		return loadConfig(null, config);
	}

	public <C> C loadConfig(String prefix, C config) {
		doLoadConfig(prefix, config);
		if (log.isDebugEnabled()) {
			log.debug("Loaded config " + config.getClass().getSimpleName() + ":\n" + ConfigInspector.dump(prefix, config, false));
		}
		if (trackable(config))
			return trackConfig(prefix, config);
		return config;
	}

	@Override
	public void bundleChanged() throws Exception {
		if (configTracker != null) {
			configTracker.updateConfigs((workingCopy, sourceConfig, trackingData) -> {
				ObservableConfig config = ConfigDuplicator.cloneConfig(trackingData.defaultConfig);
				doLoadConfig(trackingData.prefix, config);
				return config;
			});
		}
	}

	private void doLoadConfig(String prefix, Object config) {
		Collection<Field> parameterFields = getConfigParameterFields(config.getClass());
		if (parameterFields.size() == 0)
			throw new IllegalArgumentException("No configuration parameters found in class: " + config.getClass());
		loadConfigParameters(prefix, config, parameterFields);
	}

	private boolean trackable(Object config) {
		return configTracker != null && config instanceof ObservableConfig;
	}

	@SuppressWarnings("unchecked")
	private <C> C trackConfig(String prefix, C config) {
		ObservableConfig observableConfig = (ObservableConfig) config;
		TrackingData trackingData = new TrackingData(prefix, observableConfig);
		return (C) configTracker.register(observableConfig, trackingData);
	}

	private void loadConfigParameters(String prefix, Object config, Collection<Field> parameterFields) {
		for (Field field : parameterFields) {
			String key = getParameterKey(prefix, field);
			field.setAccessible(true);
			Class<?> type = getFieldType(config, field);
			List<Field> childFields = getConfigParameterFields(type);
			if (childFields.size() > 0) {
				if (type.isArray()) {
					loadArrayOfSubConfigs(config, key, field, childFields);
				} else {
					loadSubConfig(config, key, field, childFields);
				}
				continue;
			}
			if (key.length() == 0)
				throw new IllegalArgumentException("Empty key for configuration parameter: " + field);
			Object defaultValue = getFieldValue(config, field);
			Object value = loadParameterValue(key, field, defaultValue);
			setParameterValue(config, field, value);
		}
	}

	private void loadSubConfig(Object config, String key, Field field, Collection<Field> parameterFields) {
		Object value = getFieldValue(config, field);
		Class<?> type = value == null ? field.getType() : value.getClass();
		if (value == null) {
			value = newFieldInstance(field, type);
			setParameterValue(config, field, value);
		}
		loadConfigParameters(key, value, parameterFields);
	}

	private void loadArrayOfSubConfigs(Object config, String key, Field field, Collection<Field> parameterFields) {
		Object value = getFieldValue(config, field);
		Class<?> elementType = field.getType().getComponentType();
		Field identifierField = getIdentifierField(elementType);
		Set<String> ids = configBackend.getSubconfigIds(key);
		if (value == null) {
			value = Array.newInstance(elementType, ids.size());
			setParameterValue(config, field, value);
		}
		Object[] array = (Object[]) value;
		int index = 0;
		Class<?> idType = identifierField == null ? null : identifierField.getType();
		for (String id : ids) {
			Object element = newFieldInstance(field, elementType);
			if (identifierField != null) {
				identifierField.setAccessible(true);
				setParameterValue(element, identifierField, getIdentifierValue(id, idType));
			}
			loadConfigParameters(key + "." + id, element, parameterFields);
			array[index++] = element;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getIdentifierValue(String id, Class<?> idType) {
		if (idType == null || ! idType.isEnum())
			return id;
		return Enum.valueOf((Class) idType, id);
	}

	private Object loadParameterValue(String key, Field field, Object defaultValue) {
		Class<?> type = field.getType();
		if (type.isArray()) {
			type = type.getComponentType();
			Class<?> loadType = normalizeType(type);
			Object value = configBackend.getValues(key, loadType);
			if (value == null)
				return defaultValue;
			if (type.isPrimitive())
				return unboxArray(value, loadType, type);
			return value;
		} else {
			Class<?> loadType = normalizeType(type);
			Object value = configBackend.getValue(key, loadType);
			return value == null ? defaultValue : value;
		}
	}

	private void setParameterValue(Object config, Field field, Object value) {
		Method setter = findSetter(config, field);
		try {
			if (setter == null) {
				field.set(config, value);
			} else {
				setter.invoke(config, value);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
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

	private Class<?> normalizeType(Class<?> type) {
		if (type.isPrimitive())
			return primitiveWrappers.get(type);
		return type;
	}

	private Object unboxArray(Object value, Class<?> boxedType, Class<?> type) {
		if (type == boolean.class) {
			Boolean[] source = (Boolean[]) value;
			boolean[] reault = new boolean[source.length];
			for (int i = 0; i < reault.length; i++) {
				reault[i] = source[i];
			}
			return reault;
		}
		if (type == int.class) {
			Integer[] source = (Integer[]) value;
			int[] reault = new int[source.length];
			for (int i = 0; i < reault.length; i++) {
				reault[i] = source[i];
			}
			return reault;
		}
		if (type == long.class) {
			Long[] source = (Long[]) value;
			long[] reault = new long[source.length];
			for (int i = 0; i < reault.length; i++) {
				reault[i] = source[i];
			}
			return reault;
		}
		throw new IllegalStateException("Unsupported type for unbox array: " + type);
	}

	private static Map<Class<?>, Class<?>> createPrimitiveWrappers() {
		Map<Class<?>, Class<?>> primitiveWrappers = new HashMap<>();
		primitiveWrappers.put(boolean.class, Boolean.class);
		primitiveWrappers.put(int.class, Integer.class);
		primitiveWrappers.put(long.class, Long.class);
		return primitiveWrappers;
	}

	private static class TrackingData {

		private final String prefix;
		private final ObservableConfig defaultConfig;

		public TrackingData(String prefix, ObservableConfig defaultConfig) {
			this.prefix = prefix;
			this.defaultConfig = defaultConfig;
		}
	}
}
