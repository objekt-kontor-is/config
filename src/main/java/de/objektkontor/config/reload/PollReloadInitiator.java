package de.objektkontor.config.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.ReloadInitiator;

public class PollReloadInitiator extends ReloadInitiator {

    private final static Logger log = LoggerFactory.getLogger(PollReloadInitiator.class);

    private static final String DEFAULT_CHECK_INTERVAL = "30000";

    private final long checkInterval;

    public PollReloadInitiator() {
        log.info("Configuring File Touch Reload Initiator");
        checkInterval = getCheckInterval(DEFAULT_CHECK_INTERVAL);
        startThread();
        log.info("Configuration reloading enabled. Polling for changes every " + checkInterval + "ms");
    }

    private void startThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(checkInterval);
                        if (log.isDebugEnabled())
                            log.debug("Check timeout exceeded. Notifying handlers");
                        notifyHandlers();
                    }
                } catch (InterruptedException e) {
                    log.warn(Thread.currentThread().getName() + " was interrupted");
                }
            }
        };
        Thread thread = new Thread(runnable, THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }
}
