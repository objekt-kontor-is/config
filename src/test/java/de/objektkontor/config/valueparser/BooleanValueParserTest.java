package de.objektkontor.config.valueparser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BooleanValueParserTest {

	@Test
	public void testParseValue() throws Exception {
		BooleanValueParser parser = new BooleanValueParser();
		assertTrue(parser.parseValue("true", Boolean.class));
		assertTrue(!parser.parseValue("false", Boolean.class));
	}

	@Test
	public void testParseValueException() throws Exception {
		assertThrows(Exception.class, () -> {
			BooleanValueParser parser = new BooleanValueParser();
			parser.parseValue("invalid", Boolean.class);
		});
	}

	@Test
	public void teatParseValues() throws Exception {
		BooleanValueParser parser = new BooleanValueParser();
		assertArrayEquals(new Boolean[] { true, false, true }, parser.parseValues(" true, false, true ", Boolean.class));
	}

	@Test
	public void teatParseValuesException() throws Exception {
		assertThrows(Exception.class, () -> {
			BooleanValueParser parser = new BooleanValueParser();
			parser.parseValues("true, false, invalid", Boolean.class);
		});
	}
}
