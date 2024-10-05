package de.objektkontor.config.valueparser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class IntegerValueParserTest {

	@Test
	public void testParseValue() throws Exception {
		IntegerValueParser parser = new IntegerValueParser();
		assertEquals(Integer.valueOf(10), parser.parseValue("10", Integer.class));
		assertEquals(Integer.valueOf(200), parser.parseValue("200", Integer.class));
		assertEquals(Integer.valueOf(3000), parser.parseValue("3000", Integer.class));
	}

	@Test
	public void testParseValueException() throws Exception {
		assertThrows(Exception.class, () -> {
			IntegerValueParser parser = new IntegerValueParser();
			parser.parseValue("invalid", Integer.class);
		});
	}

	@Test
	public void teatParseValues() throws Exception {
		IntegerValueParser parser = new IntegerValueParser();
		assertArrayEquals(new Integer[] { 10, 200, 3000 }, parser.parseValues(" 10, 200, 3000 ", Integer.class));
	}

	@Test
	public void teatParseValuesException() throws Exception {
		assertThrows(Exception.class, () -> {
			IntegerValueParser parser = new IntegerValueParser();
			parser.parseValues("10, 200, invalid", Integer.class);
		});
	}
}
