package de.objektkontor.config;

import java.util.Set;

public interface ConfigBackend {

    public abstract <V> V getValue(String bundle, String key, Class<V> valueType) throws ValueFormatException;

    public abstract <V> V getValue(String bundle, String key, V defaultValue) throws ValueFormatException;

    public abstract <V> V[] getValues(String bundle, String key, Class<V> valueType) throws ValueFormatException;

    public abstract <V> V[] getValues(String bundle, String key, V[] defaultValues) throws ValueFormatException;

    public abstract Set<String> getSubconfigIds(String bundle, String key) throws DuplicateConfigIdException;

    public abstract void addObserver(String bundle, BundleObserver observer);

    public abstract void removeObserver(String bundle, BundleObserver observer);
}
