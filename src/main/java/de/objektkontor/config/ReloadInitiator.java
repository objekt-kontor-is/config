package de.objektkontor.config;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.reload.NoReloadInitiator;
import de.objektkontor.config.reload.PollReloadInitiator;
import de.objektkontor.config.reload.SignalReloadInitiator;
import de.objektkontor.config.reload.SocketReloadInitiator;
import de.objektkontor.config.reload.TouchReloadInitiator;

public abstract class ReloadInitiator {

    private final static Logger log = LoggerFactory.getLogger(ReloadInitiator.class);

    public enum Policy {
        NONE, SIGNAL, TOUCH, SOCKET, POLL
    }

    public interface Handler {

        public abstract void reloadConfiguration() throws Exception;
    }

    private static final String POLICY_PARAMETER = "ConfigReloadPolicy";
    private static final String CHECK_INTERVAL_PARAMETER = "ConfigReloadCheckInterval";
    protected static final String THREAD_NAME = "Config-Loader";

    private static ReloadInitiator instance;

    private final Set<Handler> handlers = new HashSet<>();

    static {
        try {
            Policy policy = getPolicy();
            log.info("Selected Initiator Policy: " + policy);
            instance = createInitiatorInstance(policy);
        } catch (Exception e) {
            log.error("Unable to create ReloadInitiator instance", e);
            instance = new NoReloadInitiator();
        }
    }

    public static ReloadInitiator getInstance() {
        return instance;
    }

    public void register(Handler handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }

    protected void notifyHandlers() {
        log.info("Reloading configuration");
        synchronized (handlers) {
            for (Handler handler : handlers)
                try {
                    handler.reloadConfiguration();
                } catch (Exception e) {
                    log.warn("Error reloading configuration. No changes applied:", e);
                }
        }
    }

    protected static String getProcessId() {
        String processId = ManagementFactory.getRuntimeMXBean().getName();
        int atPos = processId.indexOf('@');
        if (atPos != -1)
            processId = processId.substring(0, atPos);
        return processId;
    }

    protected static String getParameter(String parameterName, String defaultValue) {
        String envName = envVariableName(parameterName);
        log.info("Looking for environment variable: " + envName);
        String value = System.getenv(envName);
        if (value != null)
            return value;
        String propertyName = systemPropertyName(parameterName);
        log.info("Looking for system property: " + propertyName);
        value = System.getProperty(propertyName);
        if (value != null)
            return value;
        if (defaultValue != null)
            log.info("Using default value for " + parameterName + ": " + defaultValue);
        return defaultValue;
    }

    private static Policy getPolicy() {
        String value = getParameter(POLICY_PARAMETER, null);
        if (value == null)
            return Policy.NONE;
        return Policy.valueOf(value);
    }

    protected static long getCheckInterval(String defaultInterval) {
        String value = getParameter(CHECK_INTERVAL_PARAMETER, defaultInterval);
        return Long.parseLong(value);
    }

    private static String envVariableName(String parameterName) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parameterName.length(); i++) {
            char c = parameterName.charAt(i);
            if (i > 0 && Character.isUpperCase(c))
                result.append('_');
            result.append(c);
        }
        return result.toString().toUpperCase();
    }

    private static String systemPropertyName(String parameterName) {
        return parameterName;
    }

    private static ReloadInitiator createInitiatorInstance(Policy policy) throws Exception {
        switch (policy) {
        case SIGNAL:
            return new SignalReloadInitiator();
        case TOUCH:
            return new TouchReloadInitiator();
        case SOCKET:
            return new SocketReloadInitiator();
        case POLL:
            return new PollReloadInitiator();
        default:
            return new NoReloadInitiator();
        }
    }

    public static void main(String... args) throws Exception {
        Thread.sleep(600000);
    }
}
