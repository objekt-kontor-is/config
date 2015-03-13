package de.objektkontor.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTracker<T> extends ConfigInspector {

    private final static Logger log = LoggerFactory.getLogger(ConfigTracker.class);

    public interface Modifier<T> {

        public abstract ObservableConfig modify(ObservableConfig workingCopy, ObservableConfig sourceConfig, T trackingData);
    }

    private final Map<ObservableConfig, ObservableConfig> configs = new LinkedHashMap<>();
    private final Map<ObservableConfig, T> trackingDataValues = new HashMap<>();

    public synchronized <C extends ObservableConfig> C register(C sourceConfig) {
        return register(sourceConfig, null);
    }

    public synchronized <C extends ObservableConfig> C register(C sourceConfig, T trackingData) {
        if (sourceConfig == null)
            throw new IllegalArgumentException("Config to register cannot be null");
        C workingCopy = ConfigDuplicator.cloneConfig(sourceConfig);
        configs.put(workingCopy, sourceConfig);
        if (trackingData != null)
            trackingDataValues.put(workingCopy, trackingData);
        return workingCopy;
    }

    public synchronized ObservableConfig getSourceConfig(ObservableConfig workingCopy) {
        return configs.get(workingCopy);
    }

    public synchronized T getTrackingData(ObservableConfig workingCopy) {
        return trackingDataValues.get(workingCopy);
    }

    public synchronized void syncConfigs() throws Exception {
        performUpdate();
    }

    public synchronized void updateConfigs(Modifier<T> modifier) throws Exception {
        List<ObservableConfig> configsToUpdate = new ArrayList<>(configs.keySet());
        for (ObservableConfig workingCopy : configsToUpdate) {
            ObservableConfig sourceConfig = configs.get(workingCopy);
            T trackingData = trackingDataValues.get(workingCopy);
            sourceConfig = modifier.modify(workingCopy, sourceConfig, trackingData);
            if (sourceConfig == null)
                throw new IllegalStateException("Modify callback must return some configuration instance");
            if (sourceConfig == workingCopy)
                throw new IllegalStateException("Modify callback cannot use workingCopy itself as an updated configuration instance");
            if (! workingCopy.getClass().isAssignableFrom(sourceConfig.getClass()))
                throw new IllegalStateException("Modify callback must return instance of type which was originally registered");
            configs.put(workingCopy, sourceConfig);
        }
        performUpdate();
    }

    private void performUpdate() throws Exception {
        List<ConfigUpdate> updates = collectConfigUpdates();
        for (int index = 0; index < updates.size(); index++)
            try {
                updates.get(index).prepare();
            } catch (Exception prepareError) {
                for (int discardIndex = index; discardIndex >= 0; discardIndex--)
                    try {
                        updates.get(discardIndex).discard();
                    } catch (Throwable discardError) {
                        log.error("Uncaught exception discarding config update", discardError);
                    }
                throw prepareError;
            }
        copyUpdatedConfigParameters();
        for (ConfigUpdate update : updates)
            try {
                update.apply();
            } catch (Throwable applyError) {
                log.error("Uncaught exception applying config update", applyError);
            }
    }

    private List<ConfigUpdate> collectConfigUpdates() throws Exception {
        List<ConfigUpdate> updates = new ArrayList<>();
        for (ObservableConfig workingCopy : configs.keySet()) {
            ObservableConfig sourceConfig = configs.get(workingCopy);
            if (log.isDebugEnabled() && !ConfigComparator.deepEquals(workingCopy, sourceConfig, false))
                log.debug("Performing update for config " + workingCopy.getClass().getSimpleName() + ". Parameter values diff (old <=> new):\n" + ConfigComparator.diff(workingCopy, sourceConfig));
            notifyObservers(workingCopy, sourceConfig, updates);
        }
        return updates;
    }

    private void notifyObservers(ObservableConfig workingCopy, ObservableConfig sourceConfig, List<ConfigUpdate> updates) throws Exception {
        Class<?> type = workingCopy.getClass();
        List<Field> parameterFields = getConfigParameterFields(type);
        if (parameterFields.size() > 0 && !type.isArray())
            notifyObservers(workingCopy, sourceConfig, updates, parameterFields);
        workingCopy.notifyObserver(sourceConfig, updates);
    }

    private void notifyObservers(ObservableConfig working, ObservableConfig source, List<ConfigUpdate> updates, List<Field> parameters) throws Exception {
        for (Field field : parameters) {
            field.setAccessible(true);
            Object config = getFieldValue(working, field);
            if (config != null && config instanceof ObservableConfig) {
                ObservableConfig workingCopy = (ObservableConfig) config;
                ObservableConfig sourceConfig = (ObservableConfig) getFieldValue(source, field);
                notifyObservers(workingCopy, sourceConfig, updates);
            }
        }
    }

    private void copyUpdatedConfigParameters() {
        for (ObservableConfig workingCopy : configs.keySet()) {
            ObservableConfig sourceConfig = configs.get(workingCopy);
            ConfigDuplicator.copyConfig(sourceConfig, workingCopy);
        }
    }
}
