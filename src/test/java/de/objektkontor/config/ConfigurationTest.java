package de.objektkontor.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.objektkontor.config.Configuration.GeneralizeKeys;

public class ConfigurationTest {

    private final static Configuration configuration = new Configuration();

    @Test
    public void testGetValue() {
        assertEquals("value", configuration.getValue("testbundle", "value", String.class));
        assertEquals("default", configuration.getValue("testbundle", "unknown", "default"));
    }

    @Test
    public void testGetValues() {
        assertArrayEquals(new String[] { "value1", "value2", "value3" }, configuration.getValues("testbundle", "values", String.class));
        assertArrayEquals(new String[] { "default1", "default2" }, configuration.getValues("testbundle", "unknown", new String[] { "default1", "default2" }));
    }

    @Test
    public void testEmptyValue() {
        assertNull(configuration.getValue("testbundle", "emptyValue", String.class));
        assertNull(configuration.getValue("testbundle", "unknown", String.class));
    }

    @Test
    public void testEmptyValues() {
        assertNull(configuration.getValues("testbundle", "emptyValues", String.class));
        assertNull(configuration.getValues("testbundle", "unknown", String.class));
    }

    @Test
    public void testStageValue() {
        assertEquals("stage", configuration.getValue("testbundle", "defaultValue", String.class));
    }

    @Test
    public void testStageValues() {
        assertArrayEquals(new String[] { "stage1", "stage2", "stage3" }, configuration.getValues("testbundle", "defaultValues", String.class));
    }

    @Test
    public void testGeneralizeKeysFromBegin() {
        Configuration configuration = new Configuration(GeneralizeKeys.FROM_BEGIN);
        assertEquals("value.a.b.c", configuration.getValue("generalizekeysfrombegin1", "a.b.c", String.class));
        assertEquals("value.b.c", configuration.getValue("generalizekeysfrombegin2", "a.b.c", String.class));
        assertEquals("value.c", configuration.getValue("generalizekeysfrombegin3", "a.b.c", String.class));
    }

    @Test
    public void testGeneralizeKeysFromEnd() {
        Configuration configuration = new Configuration(GeneralizeKeys.FROM_END);
        assertEquals("value.a.b.c", configuration.getValue("generalizekeysfromend1", "a.b.c", String.class));
        assertEquals("value.a.b", configuration.getValue("generalizekeysfromend2", "a.b.c", String.class));
        assertEquals("value.a", configuration.getValue("generalizekeysfromend3", "a.b.c", String.class));
    }
}
