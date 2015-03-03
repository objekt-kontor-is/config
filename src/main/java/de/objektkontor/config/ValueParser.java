package de.objektkontor.config;

public interface ValueParser<V> {

    public abstract V parseValue(String value, Class<V> resultType) throws Exception;

    public abstract V[] parseValues(String values, Class<V> resultType) throws Exception;
}
