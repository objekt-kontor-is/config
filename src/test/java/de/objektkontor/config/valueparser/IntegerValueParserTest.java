package de.objektkontor.config.valueparser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntegerValueParserTest {

    @Test
    public void testParseValue() throws Exception {
        IntegerValueParser parser = new IntegerValueParser();
        assertEquals(new Integer(10), parser.parseValue("10", Integer.class));
        assertEquals(new Integer(200), parser.parseValue("200", Integer.class));
        assertEquals(new Integer(3000), parser.parseValue("3000", Integer.class));
    }

    @Test(expected = Exception.class)
    public void testParseValueException() throws Exception {
        IntegerValueParser parser = new IntegerValueParser();
        parser.parseValue("invalid", Integer.class);
    }

    @Test
    public void teatParseValues() throws Exception {
        IntegerValueParser parser = new IntegerValueParser();
        assertArrayEquals(new Integer[] { 10, 200, 3000 }, parser.parseValues(" 10, 200, 3000 ", Integer.class));
    }

    @Test(expected = Exception.class)
    public void teatParseValuesException() throws Exception {
        IntegerValueParser parser = new IntegerValueParser();
        parser.parseValues("10, 200, invalid", Integer.class);
    }
}
