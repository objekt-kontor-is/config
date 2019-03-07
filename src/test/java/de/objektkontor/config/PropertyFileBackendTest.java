package de.objektkontor.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import de.objektkontor.config.backend.PropertyBackend;
import de.objektkontor.config.backend.PropertyFileBackend;

public class PropertyFileBackendTest {

	private static PropertyBackend propertyBackend;

	@BeforeClass
	public static void setup() throws Exception {
		File file = File.createTempFile("test", "properties");
		try {
			InputStream in = PropertyFileBackendTest.class.getClassLoader().getResourceAsStream("testfile.properties");
			FileOutputStream out = new FileOutputStream(file);
			int data;
			while ((data = in.read()) != -1) {
				out.write(data);
			}
			out.close();
			propertyBackend = new PropertyFileBackend(file);
		} finally {
			file.delete();
		}
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
}
