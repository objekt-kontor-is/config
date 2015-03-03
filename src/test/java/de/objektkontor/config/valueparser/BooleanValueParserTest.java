package de.objektkontor.config.valueparser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanValueParserTest {

    @Test
    public void testParseValue() throws Exception {
        BooleanValueParser parser = new BooleanValueParser();
        assertTrue(parser.parseValue("true", Boolean.class));
        assertTrue(!parser.parseValue("false", Boolean.class));
    }

    @Test(expected = Exception.class)
    public void testParseValueException() throws Exception {
        BooleanValueParser parser = new BooleanValueParser();
        parser.parseValue("invalid", Boolean.class);
    }

    @Test
    public void teatParseValues() throws Exception {
        BooleanValueParser parser = new BooleanValueParser();
        assertArrayEquals(new Boolean[] { true, false, true }, parser.parseValues(" true, false, true ", Boolean.class));
    }

    @Test(expected = Exception.class)
    public void teatParseValuesException() throws Exception {
        BooleanValueParser parser = new BooleanValueParser();
        parser.parseValues("true, false, invalid", Boolean.class);
    }
}
