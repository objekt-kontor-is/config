package de.objektkontor.config;

import de.objektkontor.config.annotation.ConfigParameter;

public class TestConfig extends TestBaseConfig {

    @ConfigParameter
    private TestSubConfig subConfig;

    @ConfigParameter
    private TestSubConfig[] subConfigs;

    @Override
    public TestConfig fill() {
        super.fill();
        setSubConfig(new TestSubConfig().fill("subconfig"));
        setSubConfigs(new TestSubConfig[] { new TestSubConfig().fill("subconfig", "id1"), new TestSubConfig().fill("subconfig", "id2") });
        return this;
    }

    public TestSubConfig getSubConfig() {
        return subConfig;
    }

    public void setSubConfig(TestSubConfig subConfig) {
        this.subConfig = subConfig;
    }

    public TestSubConfig[] getSubConfigs() {
        return subConfigs;
    }

    public void setSubConfigs(TestSubConfig[] subConfigs) {
        this.subConfigs = subConfigs;
    }
}
