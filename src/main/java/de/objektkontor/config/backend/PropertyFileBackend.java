package de.objektkontor.config.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFileBackend extends PropertyBackend {

    private final static Logger log = LoggerFactory.getLogger(PropertyFileBackend.class);

    private final File file;

    public PropertyFileBackend(File file) throws Exception {
    	super(file.getName());
    	this.file = file;
        doReload();
    }

    @Override
    protected void doReload() throws Exception {
        log.debug("looking for properties in " + file);
        try (InputStream in = new FileInputStream(file)) {
        	properties.load(in);
            cleanup();
            if (log.isDebugEnabled() && !properties.isEmpty()) {
                log.debug("(Re)Loaded configuration values: " + properties.toString());
            }
        }
    }
}
