package de.objektkontor.config.valueparser;

import java.lang.reflect.Array;

import de.objektkontor.config.ValueParser;

public abstract class AbtrsactArrayParser<V> implements ValueParser<V> {

    @Override
    public V[] parseValues(String values, Class<V> resultType) throws Exception {
        String [] splittedValues = values.trim().split("\\s*,\\s*");
        V[] result = createArray(resultType, splittedValues.length);
        for (int i = 0; i < result.length; i ++)
            result[i] = parseValue(splittedValues[i], resultType);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected V[] createArray(Class<V> elementType, int size) {
        return (V[]) Array.newInstance(elementType, size);
    }
}
