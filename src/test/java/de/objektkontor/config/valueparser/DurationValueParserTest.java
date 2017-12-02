package de.objektkontor.config.valueparser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.time.Duration;

import org.junit.Test;

public class DurationValueParserTest {

    @Test
    public void testParseValue() throws Exception {
    	DurationValueParser parser = new DurationValueParser();
        assertEquals(Duration.ofSeconds(2 * 24 * 60 * 60 + 3 * 60 * 60 + 4 * 60 + 5), parser.parseValue("p2dt3h4m5s", Duration.class));
        assertEquals(Duration.ofSeconds(3 * 60 * 60 + 4 * 60 + 5), parser.parseValue("3h 4m 5s", Duration.class));
    }

    @Test(expected = Exception.class)
    public void testParseValueException() throws Exception {
    	DurationValueParser parser = new DurationValueParser();
        parser.parseValue("invalid", Duration.class);
    }

    @Test
    public void teatParseValues() throws Exception {
    	DurationValueParser parser = new DurationValueParser();
		assertArrayEquals(new Duration[] { Duration.ofSeconds(2 * 24 * 60 * 60 + 3 * 60 * 60 + 4 * 60 + 5), Duration.ofSeconds(3 * 60 * 60 + 4 * 60 + 5) }, parser.parseValues(" p2dt3h4m5s, 3h 4m 5s ", Duration.class));
    }

    @Test(expected = Exception.class)
    public void teatParseValuesException() throws Exception {
    	DurationValueParser parser = new DurationValueParser();
        parser.parseValues("p2dt3h4m5s, 3h 4m 5s, false, invalid", Duration.class);
    }
}
