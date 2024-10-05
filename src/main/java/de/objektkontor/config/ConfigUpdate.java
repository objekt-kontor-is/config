package de.objektkontor.config;

public interface ConfigUpdate {

    void prepare() throws Exception;

    void apply() throws Exception;

    void discard() throws Exception;
}
