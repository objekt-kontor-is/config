package de.objektkontor.config;

import java.util.Set;

public interface ConfigBackend {

    public abstract <V> V getValue(String key, Class<V> valueType) throws ValueFormatException;

    public abstract <V> V getValue(String key, V defaultValue) throws ValueFormatException;

    public abstract <V> V[] getValues(String key, Class<V> valueType) throws ValueFormatException;

    public abstract <V> V[] getValues(String key, V[] defaultValues) throws ValueFormatException;

    public abstract Set<String> getSubconfigIds(String key) throws DuplicateConfigIdException;

    public abstract void addObserver(BundleObserver observer);

    public abstract void removeObserver(BundleObserver observer);
}
