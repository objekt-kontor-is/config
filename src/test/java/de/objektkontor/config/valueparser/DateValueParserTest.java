package de.objektkontor.config.valueparser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DateValueParserTest {

    @Test
    public void testParseValue() throws Exception {
        DateValueParser parser = new DateValueParser();
        assertEquals(createTestDate(21, 11, 2014), parser.parseValue("21.11.2014", Date.class));
        assertEquals(createTestDate(22, 12, 2014), parser.parseValue("12/22/2014", Date.class));
    }

    @Test(expected = Exception.class)
    public void testParseValueException() throws Exception {
        DateValueParser parser = new DateValueParser();
        parser.parseValue("invalid", Date.class);
    }

    @Test
    public void teatParseValues() throws Exception {
        DateValueParser parser = new DateValueParser();
        assertArrayEquals(new Date[] { createTestDate(21, 11, 2014), createTestDate(22, 12, 2014) }, parser.parseValues(" 21.11.2014, 12/22/2014 ", Date.class));
    }

    @Test(expected = Exception.class)
    public void teatParseValuesException() throws Exception {
        DateValueParser parser = new DateValueParser();
        parser.parseValues(" 21.11.2014, 12/22/2014, invalid ", Date.class);
    }

    private Date createTestDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
