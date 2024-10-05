package de.objektkontor.config.valueparser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class EnumValueParserTest {

	private enum TestEnum {
		A, B, C
	}

	@Test
	public void testParseValue() throws Exception {
		EnumValueParser<TestEnum> parser = new EnumValueParser<>();
		assertEquals(TestEnum.A, parser.parseValue("A", TestEnum.class));
		assertEquals(TestEnum.B, parser.parseValue("B", TestEnum.class));
		assertEquals(TestEnum.C, parser.parseValue("C", TestEnum.class));
	}

	@Test
	public void testParseValueException() throws Exception {
		assertThrows(Exception.class, () -> {
			EnumValueParser<TestEnum> parser = new EnumValueParser<>();
			parser.parseValue("D", TestEnum.class);
		});
	}

	@Test
	public void teatParseValues() throws Exception {
		EnumValueParser<TestEnum> parser = new EnumValueParser<>();
		assertArrayEquals(new TestEnum[] { TestEnum.A, TestEnum.B, TestEnum.C }, parser.parseValues(" A, B, C ", TestEnum.class));
	}

	@Test
	public void teatParseValuesException() throws Exception {
		assertThrows(Exception.class, () -> {
			EnumValueParser<TestEnum> parser = new EnumValueParser<>();
			parser.parseValues(" A, B, C, D ", TestEnum.class);
		});
	}
}
