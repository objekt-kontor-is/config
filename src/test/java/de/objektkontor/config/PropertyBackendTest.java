package de.objektkontor.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PropertyBackendTest {

    private final static PropertyBackend propertyBackend = new PropertyBackend();

    @Test
    public void testGetValue() {
        assertEquals("value", propertyBackend.getValue("testbundle", "value", String.class));
        assertEquals("default", propertyBackend.getValue("testbundle", "unknown", "default"));
    }

    @Test
    public void testGetValues() {
        assertArrayEquals(new String[] { "value1", "value2", "value3" }, propertyBackend.getValues("testbundle", "values", String.class));
        assertArrayEquals(new String[] { "default1", "default2" }, propertyBackend.getValues("testbundle", "unknown", new String[] { "default1", "default2" }));
    }

    @Test
    public void testEmptyValue() {
        assertNull(propertyBackend.getValue("testbundle", "emptyValue", String.class));
        assertNull(propertyBackend.getValue("testbundle", "unknown", String.class));
    }

    @Test
    public void testEmptyValues() {
        assertNull(propertyBackend.getValues("testbundle", "emptyValues", String.class));
        assertNull(propertyBackend.getValues("testbundle", "unknown", String.class));
    }

    @Test
    public void testMergeValues() {
        assertEquals("default", propertyBackend.getValue("testbundle", "defaultValue", String.class));
        assertEquals("stage", propertyBackend.getValue("testbundle", "stageValue", String.class));
        assertEquals("root", propertyBackend.getValue("testbundle", "rootValue", String.class));
        assertArrayEquals(new String[] { "default1", "default2" }, propertyBackend.getValues("testbundle", "defaultValues", String.class));
        assertArrayEquals(new String[] { "stage1", "stage2", "stage3" }, propertyBackend.getValues("testbundle", "stageValues", String.class));
        assertArrayEquals(new String[] { "root1", "root2", "root3", "root4" }, propertyBackend.getValues("testbundle", "rootValues", String.class));

    }
}
