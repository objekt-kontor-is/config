package de.objektkontor.config.valueparser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LongValueParserTest {

    @Test
    public void testParseValue() throws Exception {
        LongValueParser parser = new LongValueParser();
        assertEquals(new Long(10l), parser.parseValue("10", Long.class));
        assertEquals(new Long(200l), parser.parseValue("200", Long.class));
        assertEquals(new Long(3000l), parser.parseValue("3000", Long.class));
    }

    @Test(expected = Exception.class)
    public void testParseValueException() throws Exception {
        LongValueParser parser = new LongValueParser();
        parser.parseValue("invalid", Long.class);
    }

    @Test
    public void teatParseValues() throws Exception {
        LongValueParser parser = new LongValueParser();
        assertArrayEquals(new Long[] { 10l, 200l, 3000l }, parser.parseValues(" 10, 200, 3000 ", Long.class));
    }

    @Test(expected = Exception.class)
    public void teatParseValuesException() throws Exception {
        LongValueParser parser = new LongValueParser();
        parser.parseValues("10, 200, invalid", Long.class);
    }
}
