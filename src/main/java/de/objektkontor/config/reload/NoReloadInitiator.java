package de.objektkontor.config.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.ReloadInitiator;

public class NoReloadInitiator extends ReloadInitiator {

    private final static Logger log = LoggerFactory.getLogger(NoReloadInitiator.class);

    public NoReloadInitiator() {
        log.info("Configuration reloading disabled");
    }
}
