package de.objektkontor.config;

import java.util.List;

public interface ConfigObserver<C extends ObservableConfig> {

    public abstract void reconfigure(C newConfig, List<ConfigUpdate> updates);
}
