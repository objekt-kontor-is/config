package de.objektkontor.config.backend;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFileBackend extends PropertyBackend {

    private final static Logger log = LoggerFactory.getLogger(PropertyFileBackend.class);

    private final File file;
    private final Properties override;

    public PropertyFileBackend(File file) throws Exception {
    	this(file, null);
    }

    public PropertyFileBackend(File file, Properties override) throws Exception {
    	super(file.getName());
    	this.file = file;
    	this.override = override;
        doReload();
    }

    @Override
    protected void doReload() throws Exception {
        log.debug("looking for properties in " + file);
        properties.putAll(loadProperties(file));
        if (override != null) {
        	log.debug("applying override properties");
        	properties.putAll(override);
        }
        cleanup();
        if (log.isDebugEnabled() && !properties.isEmpty()) {
        	log.debug("(Re)Loaded configuration values: " + properties.toString());
        }
    }
}
