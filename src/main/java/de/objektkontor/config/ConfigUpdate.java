package de.objektkontor.config;

public interface ConfigUpdate {

    public abstract void prepare() throws Exception;

    public abstract void apply();

    public abstract void discard();
}
