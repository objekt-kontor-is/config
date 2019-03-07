package de.objektkontor.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.objektkontor.config.backend.PropertyBackend;
import de.objektkontor.config.backend.PropertyClasspathBackend;

public class PropertyClasspathBackendTest {

    private static PropertyBackend propertyBackend;

    @BeforeClass
    public static void setup() throws Exception {
    	propertyBackend = new PropertyClasspathBackend("testbundle");
    }

    @Test
    public void testGetValue() {
        assertEquals("value", propertyBackend.getValue("value", String.class));
        assertEquals("default", propertyBackend.getValue("unknown", "default"));
    }

    @Test
    public void testGetValues() {
        assertArrayEquals(new String[] { "value1", "value2", "value3" }, propertyBackend.getValues("values", String.class));
        assertArrayEquals(new String[] { "default1", "default2" }, propertyBackend.getValues("unknown", new String[] { "default1", "default2" }));
    }

    @Test
    public void testEmptyValue() {
        assertNull(propertyBackend.getValue("emptyValue", String.class));
        assertNull(propertyBackend.getValue("unknown", String.class));
    }

    @Test
    public void testEmptyValues() {
        assertNull(propertyBackend.getValues("emptyValues", String.class));
        assertNull(propertyBackend.getValues("unknown", String.class));
    }

    @Test
    public void testMergeValues() {
        assertEquals("default", propertyBackend.getValue("defaultValue", String.class));
        assertEquals("stage", propertyBackend.getValue("stageValue", String.class));
        assertEquals("root", propertyBackend.getValue("rootValue", String.class));
        assertArrayEquals(new String[] { "default1", "default2" }, propertyBackend.getValues("defaultValues", String.class));
        assertArrayEquals(new String[] { "stage1", "stage2", "stage3" }, propertyBackend.getValues("stageValues", String.class));
        assertArrayEquals(new String[] { "root1", "root2", "root3", "root4" }, propertyBackend.getValues("rootValues", String.class));

    }
}
