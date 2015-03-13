package de.objektkontor.config.reload;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.ReloadInitiator;

public class TouchReloadInitiator extends ReloadInitiator {

    private final static Logger logger = LoggerFactory.getLogger(TouchReloadInitiator.class);

    private static final String FILE_PARAMETER = "ConfigReloadFile";
    private static final String DEFAULT_FILE = "reload_config_<PID>.tmp";
    private static final String DEFAULT_CHECK_INTERVAL = "1000";

    private final File file;
    private final long checkInterval;

    public TouchReloadInitiator() throws IOException {
        logger.info("Configuring File Touch Reload Initiator");
        checkInterval = getCheckInterval(DEFAULT_CHECK_INTERVAL);
        file = getFile();
        startThread();
        logger.info("Configuration reloading enabled. Use command <touch " + file + "> to reload configuration");
    }

    private File getFile() throws IOException {
        String name = getParameter(FILE_PARAMETER, DEFAULT_FILE);
        if (!name.startsWith(File.separator))
            name = System.getProperty("java.io.tmpdir") + File.separator + name;
        name = name.replace("<PID>", getProcessId());
        File file = new File(name);
        file.createNewFile();
        file.deleteOnExit();
        return file;
    }

    private void startThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long timestamp = file.lastModified();
                    while (true) {
                        Thread.sleep(checkInterval);
                        long currentTimestamp = file.lastModified();
                        if (currentTimestamp != timestamp) {
                            timestamp = currentTimestamp;
                            if (logger.isDebugEnabled())
                                logger.debug("File timestamp change detected. Notifying handlers.");
                            notifyHandlers();
                        }
                    }
                } catch (InterruptedException e) {
                    logger.warn(Thread.currentThread().getName() + " was interrupted");
                }
            }
        };
        Thread thread = new Thread(runnable, THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }
}
