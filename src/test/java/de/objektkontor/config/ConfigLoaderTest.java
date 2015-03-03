package de.objektkontor.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.objektkontor.config.annotation.ConfigIdentifier;
import de.objektkontor.config.annotation.ConfigParameter;

public class ConfigLoaderTest {

    private final Backend backend = new Configuration();
    private final ConfigLoader loader = new ConfigLoader(backend, "configloader");

    @Test
    public void testSupportedDataTypes() {
        DataTypeTestConfig config = loader.loadConfig(new DataTypeTestConfig());
        assertEquals("value", config.getStringValue());
        assertEquals(Boolean.TRUE, config.getBooleanValue());
        assertEquals(new Integer(1), config.getIntegerValue());
        assertEquals(new Long(2), config.getLongValue());
        assertEquals(createTestDate(21, 11, 2014), config.getDateValue());
        assertEquals(TestEnum.B, config.getEnumValue());
    }

    @Test
    public void testSupportedArrayTypes() {
        ArrayTypeTestConfig config = loader.loadConfig(new ArrayTypeTestConfig());
        assertArrayEquals(new String[] { "value1", "value2" }, config.getStringValues());
        assertArrayEquals(new Boolean[] { Boolean.TRUE, Boolean.FALSE }, config.getBooleanValues());
        assertArrayEquals(new Integer[] { 1, 20, 300 }, config.getIntegerValues());
        assertArrayEquals(new Long[] { 2l, 30l, 400l }, config.getLongValues());
        assertArrayEquals(new Date[] { createTestDate(21, 11, 2014), createTestDate(22, 12, 2014) }, config.getDateValues());
        assertArrayEquals(new TestEnum[] { TestEnum.A, TestEnum.C }, config.getEnumValues());
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
    public void testAutounboxing() {
        TestAutounboxingConfig config = loader.loadConfig(new TestAutounboxingConfig());
        assertTrue(config.isBooleanValue());
        assertEquals(1, config.getIntegerValue());
        assertEquals(2l, config.getLongValue());
        assertArrayEquals(new int[] { 1, 20, 300 }, config.getIntegerValues());
        assertArrayEquals(new long[] { 2l, 30l, 400l }, config.getLongValues());
        assertArrayEquals(new boolean[] { true, false }, config.getBooleanValues());
    }

    @Test
    public void testSubConfigs() {
        MainTestConfig config = loader.loadConfig(new MainTestConfig());
        assertEquals("property", config.getProperty());
        assertEquals("property1.property", config.getProperty1().getProperty());
        assertEquals("property1.property1.property", config.getProperty1().getProperty1().getProperty());
        assertEquals("property1.property2.property", config.getProperty1().getProperty2().getProperty());
        assertEquals("property2.property", config.getProperty2().getProperty());
        assertEquals("property2.property1.property", config.getProperty2().getProperty1().getProperty());
        assertEquals("property2.property2.property", config.getProperty2().getProperty2().getProperty());
    }

    @Test
    public void testCustomPropertyNames() {
        CustomPropertyTestConfig config = loader.loadConfig(new CustomPropertyTestConfig());
        assertEquals("custom", config.getProperty1());
        assertEquals("customEmptyPath", config.getProperty2().getProperty1());
        assertEquals("customEmptyEmptyPath", config.getProperty2().getProperty2().getProperty());
    }

    @Test
    public void testArrayOfSubConfigs() {
        MulticonfigTestConfig config = loader.loadConfig(new MulticonfigTestConfig());
        assertEquals(2, config.getArrayElements().length);
        assertEquals("first", config.getArrayElements()[0].getId());
        assertEquals("first.value1", config.getArrayElements()[0].getProperty1());
        assertEquals("first.value2", config.getArrayElements()[0].getProperty2());
        assertEquals("second", config.getArrayElements()[1].getId());
        assertEquals("second.value1", config.getArrayElements()[1].getProperty1());
        assertEquals("second.value2", config.getArrayElements()[1].getProperty2());
    }

    private Date createTestDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public enum TestEnum {
        A, B, C
    };

    public static class DataTypeTestConfig {

        @ConfigParameter
        private String stringValue;

        @ConfigParameter
        private Boolean booleanValue;

        @ConfigParameter
        private Integer integerValue;

        @ConfigParameter
        private Long longValue;

        @ConfigParameter
        private Date dateValue;

        @ConfigParameter
        private TestEnum enumValue;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public Boolean getBooleanValue() {
            return booleanValue;
        }

        public void setBooleanValue(Boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public Integer getIntegerValue() {
            return integerValue;
        }

        public void setIntegerValue(Integer integerValue) {
            this.integerValue = integerValue;
        }

        public Long getLongValue() {
            return longValue;
        }

        public void setLongValue(Long longValue) {
            this.longValue = longValue;
        }

        public Date getDateValue() {
            return dateValue;
        }

        public void setDateValue(Date dateValue) {
            this.dateValue = dateValue;
        }

        public TestEnum getEnumValue() {
            return enumValue;
        }

        public void setEnumValue(TestEnum enumValue) {
            this.enumValue = enumValue;
        }
    }

    public static class ArrayTypeTestConfig {

        @ConfigParameter
        private String[] stringValues;

        @ConfigParameter
        private Boolean[] booleanValues;

        @ConfigParameter
        private Integer[] integerValues;

        @ConfigParameter
        private Long[] longValues;

        @ConfigParameter
        private Date[] dateValues;

        @ConfigParameter
        private TestEnum[] enumValues;

        public String[] getStringValues() {
            return stringValues;
        }

        public void setStringValues(String[] stringValues) {
            this.stringValues = stringValues;
        }

        public Boolean[] getBooleanValues() {
            return booleanValues;
        }

        public void setBooleanValues(Boolean[] booleanValues) {
            this.booleanValues = booleanValues;
        }

        public Integer[] getIntegerValues() {
            return integerValues;
        }

        public void setIntegerValues(Integer[] integerValues) {
            this.integerValues = integerValues;
        }

        public Long[] getLongValues() {
            return longValues;
        }

        public void setLongValues(Long[] longValues) {
            this.longValues = longValues;
        }

        public Date[] getDateValues() {
            return dateValues;
        }

        public void setDateValues(Date[] dateValues) {
            this.dateValues = dateValues;
        }

        public TestEnum[] getEnumValues() {
            return enumValues;
        }

        public void setEnumValues(TestEnum[] enumValues) {
            this.enumValues = enumValues;
        }
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

    public static class TestAutounboxingConfig {

        @ConfigParameter
        private boolean booleanValue;

        @ConfigParameter
        private int integerValue;

        @ConfigParameter
        private int longValue;

        @ConfigParameter
        private boolean[] booleanValues;

        @ConfigParameter
        private int[] integerValues;

        @ConfigParameter
        private long[] longValues;

        public boolean isBooleanValue() {
            return booleanValue;
        }

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public int getIntegerValue() {
            return integerValue;
        }

        public void setIntegerValue(int integerValue) {
            this.integerValue = integerValue;
        }

        public int getLongValue() {
            return longValue;
        }

        public void setLongValue(int longValue) {
            this.longValue = longValue;
        }

        public boolean[] getBooleanValues() {
            return booleanValues;
        }

        public void setBooleanValues(boolean[] booleanValues) {
            this.booleanValues = booleanValues;
        }

        public int[] getIntegerValues() {
            return integerValues;
        }

        public void setIntegerValues(int[] integerValues) {
            this.integerValues = integerValues;
        }

        public long[] getLongValues() {
            return longValues;
        }

        public void setLongValues(long[] longValues) {
            this.longValues = longValues;
        }
    }

    public static class MainTestConfig {

        @ConfigParameter
        private String property;

        @ConfigParameter
        SubTestConfig property1 = new SubTestConfig();

        @ConfigParameter
        SubTestConfig property2;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public SubTestConfig getProperty1() {
            return property1;
        }

        public void setProperty1(SubTestConfig subProperty1) {
            property1 = subProperty1;
        }

        public SubTestConfig getProperty2() {
            return property2;
        }

        public void setProperty2(SubTestConfig subProperty2) {
            property2 = subProperty2;
        }
    }

    public static class SubTestConfig {

        @ConfigParameter
        private String property;

        @ConfigParameter
        SubSubTestConfig property1 = new SubSubTestConfig();

        @ConfigParameter
        SubSubTestConfig property2;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public SubSubTestConfig getProperty1() {
            return property1;
        }

        public void setProperty1(SubSubTestConfig subProperty1) {
            property1 = subProperty1;
        }

        public SubSubTestConfig getProperty2() {
            return property2;
        }

        public void setProperty2(SubSubTestConfig subProperty2) {
            property2 = subProperty2;
        }
    }

    public static class SubSubTestConfig {

        @ConfigParameter
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
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

    public static class MulticonfigTestConfig {

        @ConfigParameter("elements")
        private ObjectArrayElementTestConfig[] arrayElements;

        public ObjectArrayElementTestConfig[] getArrayElements() {
            return arrayElements;
        }

        public void setArrayElements(ObjectArrayElementTestConfig[] elements) {
            arrayElements = elements;
        }
    }

    public static class ObjectArrayElementTestConfig {

        @ConfigIdentifier
        private String id;

        @ConfigParameter
        private String property1;

        @ConfigParameter
        private String property2;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }
}
