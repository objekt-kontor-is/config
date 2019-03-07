package de.objektkontor.config.backend;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyClasspathBackend extends PropertyBackend {

    private final static Logger log = LoggerFactory.getLogger(PropertyClasspathBackend.class);

    private final ClassLoader classLoader;

    public PropertyClasspathBackend(String sourceName) throws Exception {
    	this(null, sourceName);
    }

    public PropertyClasspathBackend(ClassLoader classLoader, String sourceName) throws Exception {
    	super(sourceName);
        this.classLoader = classLoader;
        doReload();
    }

    @Override
    protected void doReload() throws Exception {
        ClassLoader loader = classLoader;
        if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}
        if (loader == null) {
			loader = PropertyClasspathBackend.class.getClassLoader();
		}
        try (InputStream defaultBundle = loader.getResourceAsStream("default/" + sourceName + ".properties");
                InputStream stageBundle = loader.getResourceAsStream("stage/" + sourceName + ".properties");
                InputStream rootBundle = loader.getResourceAsStream(sourceName + ".properties");)
        {
            if (defaultBundle == null && stageBundle == null && rootBundle == null) {
				log.warn("No property files found in classpath for bundle: " + sourceName);
			}
            if (defaultBundle != null) {
                log.debug("default bundle " + sourceName + " found");
                properties.load(defaultBundle);
            }
            if (stageBundle != null) {
                log.debug("stage bundle " + sourceName + " found");
                properties.load(stageBundle);
            }
            if (rootBundle != null) {
                log.debug("root bundle " + sourceName + " found");
                properties.load(rootBundle);
            }
            cleanup();
            if (log.isDebugEnabled() && !properties.isEmpty()) {
                log.debug("(Re)Loaded configuration values for bundle: " + sourceName);
                log.debug(properties.toString());
            }
        }
    }
}
