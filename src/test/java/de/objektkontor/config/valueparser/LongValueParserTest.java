package de.objektkontor.config.valueparser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class LongValueParserTest {

	@Test
	public void testParseValue() throws Exception {
		LongValueParser parser = new LongValueParser();
		assertEquals(Long.valueOf(10l), parser.parseValue("10", Long.class));
		assertEquals(Long.valueOf(200l), parser.parseValue("200", Long.class));
		assertEquals(Long.valueOf(3000l), parser.parseValue("3000", Long.class));
	}

	@Test
	public void testParseValueException() throws Exception {
		assertThrows(Exception.class, () -> {
			LongValueParser parser = new LongValueParser();
			parser.parseValue("invalid", Long.class);
		});
	}

	@Test
	public void teatParseValues() throws Exception {
		LongValueParser parser = new LongValueParser();
		assertArrayEquals(new Long[] { 10l, 200l, 3000l }, parser.parseValues(" 10, 200, 3000 ", Long.class));
	}

	@Test
	public void teatParseValuesException() throws Exception {
		assertThrows(Exception.class, () -> {
			LongValueParser parser = new LongValueParser();
			parser.parseValues("10, 200, invalid", Long.class);
		});
	}
}
