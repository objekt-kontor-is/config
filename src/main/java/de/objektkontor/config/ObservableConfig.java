package de.objektkontor.config;

import java.util.List;

public abstract class ObservableConfig {

    private ConfigObserver<?> observer;

    public boolean hasObserver() {
        return observer != null;
    }

    public void setObserver(ConfigObserver<?> observer) {
        if (observer == null)
            throw new IllegalArgumentException("observer cannot be null");
        if (this.observer != null)
            throw new IllegalStateException("observer allready set");
        this.observer = observer;
    }

    public ConfigObserver<?> removeObserver() {
        ConfigObserver<?> removedObserver = observer;
        observer = null;
        return removedObserver;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void notifyObserver(ObservableConfig newConfig, List<ConfigUpdate> updates) throws Exception {
        if (observer == null)
            return;
        if (ConfigComparator.equals(this, newConfig))
            return;
        ((ConfigObserver) observer).reconfigure(newConfig, updates);
    }
}
