package de.objektkontor.config.valueparser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringValueParserTest {

    @Test
    public void testParseValue() throws Exception {
        StringValueParser parser = new StringValueParser();
        assertEquals("value1", parser.parseValue("value1", String.class));
        assertEquals("value2", parser.parseValue("value2", String.class));
        assertEquals("value3", parser.parseValue("value3", String.class));
    }

    @Test
    public void teatParseValues() throws Exception {
        StringValueParser parser = new StringValueParser();
        assertArrayEquals(new String[] { "value1", "value 2", "v a l u e 3" }, parser.parseValues(" value1 , value 2 , v a l u e 3 ", String.class));
    }
}
