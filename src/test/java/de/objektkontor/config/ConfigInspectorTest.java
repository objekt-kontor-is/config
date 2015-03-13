package de.objektkontor.config;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConfigInspectorTest {

    @Test
    public void testDumpWorksWithoutExceptions() {
        TestConfig config = new TestConfig();
        assertTrue(ConfigInspector.dump(config).length() > 0);

        config = new TestConfig().fill();
        assertTrue(ConfigInspector.dump(config).length() > 0);

        config = new TestConfig().fill();
        config.getStringValues()[0] = null;
        config.getSubConfigs()[0] = null;
        assertTrue(ConfigInspector.dump(config).length() > 0);

        //System.out.println(ConfigInspector.dump(config));
    }
}
