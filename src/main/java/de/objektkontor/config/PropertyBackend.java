package de.objektkontor.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyBackend extends AbstractConfigBackend {

    private final static Logger log = LoggerFactory.getLogger(PropertyBackend.class);

    protected final ClassLoader classLoader;

    private final Map<String, Bundle> bundles = new ConcurrentHashMap<String, Bundle>();

    public PropertyBackend() {
        this(null);
    }

    public PropertyBackend(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public <V> V getValue(String bundle, String key, Class<V> valueType) throws ValueFormatException {
        String value = getBundleValue(bundle, key);
        if (value == null)
            return null;
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValue(value, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    @Override
    public <V> V getValue(String bundle, String key, V defaultValue) throws ValueFormatException {
        String value = getBundleValue(bundle, key);
        if (value == null)
            return defaultValue;
        @SuppressWarnings("unchecked")
        Class<V> valueType = (Class<V>) defaultValue.getClass();
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValue(value, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    @Override
    public <V> V[] getValues(String bundle, String key, Class<V> valueType) throws ValueFormatException {
        String values = getBundleValue(bundle, key);
        if (values == null)
            return null;
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValues(values, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    @Override
    public <V> V[] getValues(String bundle, String key, V[] defaultValues) throws ValueFormatException {
        String values = getBundleValue(bundle, key);
        if (values == null)
            return defaultValues;
        @SuppressWarnings("unchecked")
        Class<V> valueType = (Class<V>) defaultValues.getClass().getComponentType();
        ValueParser<V> parser = getParser(valueType);
        try {
            return parser.parseValues(values, valueType);
        } catch (Exception e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    @Override
    public Set<String> getSubconfigIds(String bundle, String key) throws DuplicateConfigIdException {
        Set<String> result = new LinkedHashSet<>();
        String [] ids = getValues(bundle, key, String.class);
        if (ids == null)
            return result;
        for (String id : ids) {
            if (result.contains(id))
                throw new DuplicateConfigIdException(id);
            result.add(id);
        }
        return result;
    }

    @Override
    protected void doReload() {
        synchronized (bundles) {
            for (String bundleName : bundles.keySet()) {
                Bundle bundle = new Bundle(bundleName);
                bundles.put(bundleName, bundle);
            }
        }
    }

    private String getBundleValue(String bundleName, String key) {
        Bundle bundle = bundles.get(bundleName);
        if (bundle == null)
            synchronized (bundles) {
                bundle = bundles.get(bundleName);
                if (bundle == null) {
                    bundle = new Bundle(bundleName);
                    bundles.put(bundleName, bundle);
                }
            }
        return bundle.getProperty(key);
    }

    public class Bundle {

        private final Properties properties;

        public String getProperty(String key) {
            return properties.getProperty(key);
        }

        public boolean containsKey(String key) {
            return properties.containsKey(key);
        }

        public Bundle(String name) {
            properties = new Properties();
            ClassLoader loader = classLoader;
            if (loader == null)
                loader = Thread.currentThread().getContextClassLoader();
            if (loader == null)
                loader = PropertyBackend.class.getClassLoader();
            try (InputStream defaultBundle = loader.getResourceAsStream("default/" + name + ".properties");
                    InputStream stageBundle = loader.getResourceAsStream("stage/" + name + ".properties");
                    InputStream rootBundle = loader.getResourceAsStream(name + ".properties");)
            {
                if (defaultBundle == null && stageBundle == null && rootBundle == null)
                    log.warn("No property files found in classpath for bundle: " + name);
                if (defaultBundle != null) {
                    log.debug("default bundle " + name + " found");
                    load(defaultBundle, name);
                }
                if (stageBundle != null) {
                    log.debug("stage bundle " + name + " found");
                    load(stageBundle, name);
                }
                if (rootBundle != null) {
                    log.debug("root bundle " + name + " found");
                    load(rootBundle, name);
                }
                if (log.isDebugEnabled() && !properties.isEmpty()) {
                    log.debug("Loaded configuration values for bundle: " + name);
                    log.debug(properties.toString());
                }
                cleanup();
            } catch (IOException e) {
            }
        }

        private void load(InputStream in, String name) {
            try {
                properties.load(in);
            } catch (IOException e) {
                log.error("Error loading default configuration bundle: " + name, e);
            }
        }

        private void cleanup() {
            Iterator<Object> i = properties.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String value = properties.getProperty(key);
                if (value != null && (value.isEmpty() || value.matches("\\s+")))
                    i.remove();
            }
        }
    }
}
