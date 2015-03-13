package de.objektkontor.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.Test;

import de.objektkontor.config.annotation.ConfigParameter;

public class ConfigLoaderTest {

    private final ConfigBackend configBackend = new PropertyBackend();
    private final ConfigLoader loader = new ConfigLoader(configBackend, "configloader");

    @Test
    public void testSupportedDataTypes() {
        TestConfig config = loader.loadConfig(new TestConfig());
        assertEquals("value", config.getStringValue());
        assertEquals(Boolean.TRUE, config.getBooleanValue());
        assertEquals(new Integer(1), config.getIntegerValue());
        assertEquals(new Long(2), config.getLongValue());
        assertEquals(TestBaseConfig.createTestDate(21, 11, 2014), config.getDateValue());
        assertEquals(TestEnum.B, config.getEnumValue());
    }

    @Test
    public void testSupportedArrayTypes() {
        TestConfig config = loader.loadConfig(new TestConfig());
        assertArrayEquals(new String[] { "value1", "value2" }, config.getStringValues());
        assertArrayEquals(new Boolean[] { Boolean.TRUE, Boolean.FALSE }, config.getBooleanValues());
        assertArrayEquals(new Integer[] { 1, 20, 300 }, config.getIntegerValues());
        assertArrayEquals(new Long[] { 2l, 30l, 400l }, config.getLongValues());
        assertArrayEquals(new Date[] { TestBaseConfig.createTestDate(21, 11, 2014), TestBaseConfig.createTestDate(22, 12, 2014) }, config.getDateValues());
        assertArrayEquals(new TestEnum[] { TestEnum.A, TestEnum.C }, config.getEnumValues());
    }

    @Test
    public void testAutounboxing() {
        TestConfig config = loader.loadConfig(new TestConfig());
        assertTrue(config.isBooleanPrimitiveValue());
        assertEquals(1, config.getIntegerPrimitiveValue());
        assertEquals(2l, config.getLongPrimitiveValue());
        assertArrayEquals(new boolean[] { true, false }, config.getBooleanPrimitiveValues());
        assertArrayEquals(new int[] { 1, 20, 300 }, config.getIntegerPrimitiveValues());
        assertArrayEquals(new long[] { 2l, 30l, 400l }, config.getLongPrimitiveValues());
    }

    @Test
    public void testSubConfigs() {
        TestConfig config = loader.loadConfig(new TestConfig());
        assertEquals("value", config.getStringValue());
        assertEquals("subconfig.value", config.getSubConfig().getStringValue());
        assertEquals("subconfig.subconfig.value", config.getSubConfig().getSubConfig().getStringValue());
    }

    @Test
    public void testArrayOfSubConfigs() {
        TestConfig config = loader.loadConfig(new TestConfig());
        assertEquals(2, config.getSubConfigs().length);
        assertEquals("value", config.getStringValue());
        assertEquals("first", config.getSubConfigs()[0].getId());
        assertEquals("first.value", config.getSubConfigs()[0].getStringValue());
        assertEquals("second", config.getSubConfigs()[1].getId());
        assertEquals("second.value", config.getSubConfigs()[1].getStringValue());
    }

    @Test
    public void testDefaultValues() {
        DefaultValueTestConfig config = loader.loadConfig(new DefaultValueTestConfig());
        assertEquals("value", config.getStringValue());
        assertEquals("default", config.getUnknownValue());
        assertArrayEquals(new String[] { "value1", "value2" }, config.getStringValues());
        assertArrayEquals(new String[] { "default1", "default2" }, config.getUnknownValues());
    }

    @Test
    public void testCustomPropertyNames() {
        CustomPropertyTestConfig config = loader.loadConfig(new CustomPropertyTestConfig());
        assertEquals("custom", config.getProperty1());
        assertEquals("customEmptyPath", config.getProperty2().getProperty1());
        assertEquals("customEmptyEmptyPath", config.getProperty2().getProperty2().getProperty());
    }

    @Test
    public void testLoadConfigUsesTrackerIfEnabled() throws Exception {
        ConfigLoader trackingLoader = new ConfigLoader(configBackend, "configloader", true);
        TestConfig defaultConfig = new TestConfig();
        defaultConfig.setStringValue("default");
        TestConfig config = trackingLoader.loadConfig("tracker", defaultConfig);
        assertEquals("default", config.getStringValue());
        assertEquals("loaded", config.getSubConfig().getStringValue());
        TestConfig origConfig = ConfigDuplicator.cloneConfig(config);

        config.setStringValue("modified");
        config.getSubConfig().setStringValue("modified");
        @SuppressWarnings("unchecked")
        ConfigObserver<TestConfig> observer = mock(ConfigObserver.class);
        config.setObserver(observer);

        trackingLoader.bundleChanged();

        verify(observer).reconfigure(eq(origConfig), anyListOf(ConfigUpdate.class));
    }

    public static class DefaultValueTestConfig {

        @ConfigParameter
        private String stringValue = "default";

        @ConfigParameter
        private String[] stringValues = new String[] { "default1", "default2" };

        @ConfigParameter
        private String unknownValue = "default";

        @ConfigParameter
        private String[] unknownValues = new String[] { "default1", "default2" };

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public String[] getStringValues() {
            return stringValues;
        }

        public void setStringValues(String[] stringValues) {
            this.stringValues = stringValues;
        }

        public String getUnknownValue() {
            return unknownValue;
        }

        public void setUnknownValue(String unknownValue) {
            this.unknownValue = unknownValue;
        }

        public String[] getUnknownValues() {
            return unknownValues;
        }

        public void setUnknownValues(String[] unknownValues) {
            this.unknownValues = unknownValues;
        }
    }

    public static class CustomPropertyTestConfig {

        @ConfigParameter("customName")
        private String property1;

        @ConfigParameter("")
        EmptySubPropertyTestConfig property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public EmptySubPropertyTestConfig getProperty2() {
            return property2;
        }

        public void setProperty2(EmptySubPropertyTestConfig property2) {
            this.property2 = property2;
        }
    }

    public static class EmptySubPropertyTestConfig {

        @ConfigParameter("customEmptyPath")
        private String property1;

        @ConfigParameter("")
        EmptySubSubPropertyTestConfig property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public EmptySubSubPropertyTestConfig getProperty2() {
            return property2;
        }

        public void setProperty2(EmptySubSubPropertyTestConfig property2) {
            this.property2 = property2;
        }
    }

    public static class EmptySubSubPropertyTestConfig {

        @ConfigParameter("customEmptyEmptyPath")
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }
}
