package de.objektkontor.config.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import de.objektkontor.config.ReloadInitiator;

@SuppressWarnings("restriction")
public class SignalReloadInitiator extends ReloadInitiator {

    private final static Logger log = LoggerFactory.getLogger(SignalReloadInitiator.class);

    private static final String SIGNAL_PARAMETER = "ConfigReloadSignal";
    private static final String DEFAULT_SIGNAL = "USR2";

    public SignalReloadInitiator() {
        log.info("Configuring Signal Reload Initiator");
        String signal = getSignalName();
        Signal.handle(new Signal(signal), new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                if (log.isDebugEnabled())
                    log.debug("Signal received. Notifying handlers.");
                notifyHandlers();
            }
        });
        log.info("Configuration reloading enabled. Use command <kill -s SIG" + signal + " " + getProcessId() + "> to reload configuration");
    }

    private String getSignalName() {
        String name = getParameter(SIGNAL_PARAMETER, DEFAULT_SIGNAL);
        if (name.startsWith("SIG"))
            return name.substring(3);
        return name;
    }
}
