package de.objektkontor.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ConfigDuplicatorTest {

    @Test
    public void testCopyConfigComplettlyFillsAnEmptyTarget() {
        TestConfigs configs = new TestConfigs();
        assertConfigsEquals(false, configs);
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCopyConfigWithDifferentValueArrayLegth() {
        TestConfigs configs = new TestConfigs().fill();
        configs.target.getSubConfig().getSubConfig().setStringValues(new String[] { "different" });
        assertConfigsEquals(false, configs);
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCopyConfigCorrectlyHandlesNullValue() {
        TestConfigs configs = new TestConfigs().fill();
        configs.source.setStringValue(null);
        assertNotNull(configs.target.getStringValue());
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertNull(configs.target.getStringValue());
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCopyConfigCorrectlyHandlesNullArrayValue() {
        TestConfigs configs = new TestConfigs().fill();
        configs.source.setStringValues(null);
        assertNotNull(configs.target.getStringValues());
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertNull(configs.target.getStringValues());
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCopyConfigCorrectlyHandlesNullSubConfig() {
        TestConfigs configs = new TestConfigs().fill();
        configs.source.setSubConfig(null);
        assertNotNull(configs.target.getSubConfig());
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertNull(configs.target.getSubConfig());
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCopyConfigCorrectlyHandlesNullArrayOfSubConfigs() {
        TestConfigs configs = new TestConfigs().fill();
        configs.source.setSubConfigs(null);
        assertNotNull(configs.target.getSubConfig());
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertNull(configs.target.getSubConfigs());
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCopyConfigWithDifferentConfigArrayLegth() {
        TestConfigs configs = new TestConfigs().fill();
        configs.target.setSubConfigs(new TestSubConfig[] { new TestSubConfig().fill("subconfig", "id1") });
        assertConfigsEquals(false, configs);
        ConfigDuplicator.copyConfig(configs.source, configs.target);
        assertConfigsEquals(true, configs);
    }

    @Test
    public void testCloneConfigFromEmptyConfig() {
        TestConfig source = new TestConfig();
        TestConfig clone = ConfigDuplicator.cloneConfig(source);
        assertTrue(ConfigComparator.deepEquals(source, clone));
        assertTrue(ConfigComparator.deepEquals(clone, source));
    }

    @Test
    public void testCloneConfigFromFilledConfig() {
        TestConfig source = new TestConfig().fill();
        TestConfig clone = ConfigDuplicator.cloneConfig(source);
        assertTrue(ConfigComparator.deepEquals(source, clone));
        assertTrue(ConfigComparator.deepEquals(clone, source));
    }

    @Test
    public void testCompleteConfigDoesNotChangeTemplateForEmptyConfig() {
    	TestConfig template = new TestConfig().fill();
    	TestConfig config = new TestConfig();
    	TestConfig result = ConfigDuplicator.completeConfig(config, template);
    	assertTrue(ConfigComparator.deepEquals(result, template));
    }

    @Test
    public void testCompleteConfigAppliesConfigValuesToTemplate() {
    	TestConfig template = new TestConfig().fill();
    	TestConfig config = new TestConfig().fill("changed");
    	TestConfig result = ConfigDuplicator.completeConfig(config, template);
    	assertTrue(ConfigComparator.deepEquals(result, config));
    }

    @Test
    public void testCompleteConfigDoesNotOverwritesTemplateValuesWithDefaults() {
    	TestConfig template = new TestConfig().fill();
    	TestConfig config = new TestConfig().fill("changed");
    	TestConfig reference = ConfigDuplicator.cloneConfig(config);
    	config.setBooleanPrimitiveValue(false);
    	config.setIntegerPrimitiveValue(0);
    	config.setLongPrimitiveValue(0);
    	config.getSubConfig().setBooleanPrimitiveValue(false);
    	config.getSubConfig().setIntegerPrimitiveValue(0);
    	config.getSubConfig().setLongPrimitiveValue(0);
    	config.getSubConfigs()[0].setBooleanPrimitiveValue(false);
    	config.getSubConfigs()[0].setIntegerPrimitiveValue(0);
    	config.getSubConfigs()[0].setLongPrimitiveValue(0);
    	TestConfig result = ConfigDuplicator.completeConfig(config, template);
    	assertTrue(ConfigComparator.deepEquals(result, reference));
    }

    private void assertConfigsEquals(boolean expectedEqualsResult, TestConfigs configs) {
        assertEquals(expectedEqualsResult, ConfigComparator.deepEquals(configs.source, configs.target));
    }

    private static class TestConfigs {

        final TestConfig source = new TestConfig().fill();
        final TestConfig target = new TestConfig();

        public TestConfigs fill() {
            target.fill();
            return this;
        }
    }
}
