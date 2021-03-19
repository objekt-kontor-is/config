package de.objektkontor.config.backend;

import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.objektkontor.config.BundleObserver;
import de.objektkontor.config.ConfigBackend;
import de.objektkontor.config.ReloadInitiator;
import de.objektkontor.config.ValueParser;
import de.objektkontor.config.valueparser.BooleanValueParser;
import de.objektkontor.config.valueparser.DateValueParser;
import de.objektkontor.config.valueparser.DurationValueParser;
import de.objektkontor.config.valueparser.EnumValueParser;
import de.objektkontor.config.valueparser.IntegerValueParser;
import de.objektkontor.config.valueparser.LongValueParser;
import de.objektkontor.config.valueparser.StringValueParser;
import de.objektkontor.config.valueparser.URLValueParser;

public abstract class AbstractConfigBackend implements ConfigBackend, ReloadInitiator.Handler {

    protected final Set<BundleObserver> observers = new LinkedHashSet<>();

    private final static Map<Class<?>, ValueParser<?>> parsers = createParsers();

    public AbstractConfigBackend() {
        ReloadInitiator.getInstance().register(this);
    }

    protected abstract void doReload() throws Exception;

    @Override
    public void reloadConfiguration() throws Exception {
        doReload();
        synchronized (observers) {
            for (BundleObserver observer : observers) {
				observer.bundleChanged();
			}
        }
    }

    @Override
    public void addObserver(BundleObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(BundleObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    @SuppressWarnings("unchecked")
    protected static <V> ValueParser<V> getParser(Class<V> valueType) {
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

    @SuppressWarnings("rawtypes")
    private static Map<Class<?>, ValueParser<?>> createParsers() {
        Map<Class<?>, ValueParser<?>> parsers = new HashMap<>();
        parsers.put(String.class, new StringValueParser());
        parsers.put(Boolean.class, new BooleanValueParser());
        parsers.put(Integer.class, new IntegerValueParser());
        parsers.put(Long.class, new LongValueParser());
        parsers.put(Date.class, new DateValueParser());
        parsers.put(Enum.class, new EnumValueParser());
        parsers.put(Duration.class, new DurationValueParser());
        parsers.put(URL.class, new URLValueParser());
        return parsers;
    }
}
