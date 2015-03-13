package de.objektkontor.config;

import de.objektkontor.config.annotation.ConfigIdentifier;
import de.objektkontor.config.annotation.ConfigParameter;

public class TestSubConfig extends TestBaseConfig {

    @ConfigIdentifier
    private String id;

    @ConfigParameter
    private TestBaseConfig subConfig;

    @ConfigParameter
    private TestBaseConfig[] subConfigs;

    public String getId() {
        return id;
    }

    @Override
    public TestSubConfig fill() {
        return fill(null, null);
    }

    @Override
    public TestSubConfig fill(String prefix) {
        return fill(prefix, null);
    }

    public TestSubConfig fill(String prefix, String id) {
        super.fill(prefix);
        setId(id);
        setSubConfig(new TestBaseConfig().fill(prefix(prefix, "subconfig")));
        setSubConfigs(new TestBaseConfig[] { new TestBaseConfig().fill(prefix(prefix, "subconfig")), new TestBaseConfig().fill(prefix(prefix, "subconfig")) });
        return this;
    }

    private String prefix(String prefix, String value) {
        return prefix == null ? value : prefix + "." + value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TestBaseConfig getSubConfig() {
        return subConfig;
    }

    public void setSubConfig(TestBaseConfig subConfig) {
        this.subConfig = subConfig;
    }

    public TestBaseConfig[] getSubConfigs() {
        return subConfigs;
    }

    public void setSubConfigs(TestBaseConfig[] subConfigs) {
        this.subConfigs = subConfigs;
    }
}