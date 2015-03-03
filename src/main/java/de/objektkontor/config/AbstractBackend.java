package de.objektkontor.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.objektkontor.config.valueparser.BooleanValueParser;
import de.objektkontor.config.valueparser.DateValueParser;
import de.objektkontor.config.valueparser.EnumValueParser;
import de.objektkontor.config.valueparser.IntegerValueParser;
import de.objektkontor.config.valueparser.LongValueParser;
import de.objektkontor.config.valueparser.StringValueParser;

public abstract class AbstractBackend implements Backend {

    protected final ClassLoader classLoader;

    private final static Map<Class<?>, ValueParser<?>> parsers = createParsers();

    public AbstractBackend(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class<?>, ValueParser<?>> createParsers() {
        Map<Class<?>, ValueParser<?>> parsers = new HashMap<>();
        parsers.put(String.class, new StringValueParser());
        parsers.put(Boolean.class, new BooleanValueParser());
        parsers.put(Integer.class, new IntegerValueParser());
        parsers.put(Long.class, new LongValueParser());
        parsers.put(Date.class, new DateValueParser());
        parsers.put(Enum.class, new EnumValueParser());
        return parsers;
    }

    @SuppressWarnings("unchecked")
    public static <V> ValueParser<V> getParser(Class<V> valueType) {
        Class<?> searchType = normalizeType(valueType);
        ValueParser<?> parser = parsers.get(searchType);
        if (parser == null)
            throw new IllegalArgumentException("Datatype is not supported: " + valueType);
        return (ValueParser<V>) parser;
    }

    private static Class<?> normalizeType(Class<?> type) {
        if (type.getSuperclass() == Enum.class)
            return Enum.class;
        return type;
    }
}
