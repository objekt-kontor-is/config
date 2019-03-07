package de.objektkontor.config.backend;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import de.objektkontor.config.DuplicateConfigIdException;
import de.objektkontor.config.ValueFormatException;
import de.objektkontor.config.ValueParser;

public abstract class PropertyBackend extends AbstractConfigBackend {

    protected final String sourceName;
    protected final Properties properties = new Properties();

    protected PropertyBackend(String sourceName) {
    	this.sourceName = sourceName;
    }

    @Override
    public <V> V getValue(String key, Class<V> valueType) throws ValueFormatException {
        String value = properties.getProperty(key);
        if (value == null)
            return null;
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValue(value, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(sourceName, key, e);
        }
    }

    @Override
    public <V> V getValue(String key, V defaultValue) throws ValueFormatException {
        String value = properties.getProperty(key);
        if (value == null)
            return defaultValue;
        @SuppressWarnings("unchecked")
        Class<V> valueType = (Class<V>) defaultValue.getClass();
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValue(value, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(sourceName, key, e);
        }
    }

    @Override
    public <V> V[] getValues(String key, Class<V> valueType) throws ValueFormatException {
        String values = properties.getProperty(key);
        if (values == null)
            return null;
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValues(values, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(sourceName, key, e);
        }
    }

    @Override
    public <V> V[] getValues(String key, V[] defaultValues) throws ValueFormatException {
        String values = properties.getProperty(key);
        if (values == null)
            return defaultValues;
        @SuppressWarnings("unchecked")
        Class<V> valueType = (Class<V>) defaultValues.getClass().getComponentType();
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValues(values, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(sourceName, key, e);
        }
    }

    @Override
    public Set<String> getSubconfigIds(String key) throws DuplicateConfigIdException {
        Set<String> result = new LinkedHashSet<>();
        String [] ids = getValues(key, String.class);
        if (ids == null)
            return result;
        for (String id : ids) {
            if (result.contains(id))
                throw new DuplicateConfigIdException(id);
            result.add(id);
        }
        return result;
    }

    protected void cleanup() {
        Iterator<Object> i = properties.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            String value = properties.getProperty(key);
            if (value != null && (value.isEmpty() || value.matches("\\s+"))) {
				i.remove();
			}
        }
    }
}
