package de.objektkontor.config.valueparser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateValueParser extends AbtrsactArrayParser<Date> {

    private final static DateFormat [] dateParsers = { new SimpleDateFormat("dd.MM.yyyy"), new SimpleDateFormat("MM/dd/yyyy") };

    @Override
    public Date parseValue(String value, Class<Date> resultType) throws Exception {
        StringBuilder parseErrors = null;
        for (DateFormat parser : dateParsers)
            synchronized (parser) {
                try {
                    return parser.parse(value);
                } catch (ParseException e) {
                    if (parseErrors == null)
                        parseErrors = new StringBuilder(e.getMessage());
                    else
                        parseErrors.append(", ").append(e.getMessage());
                }
            }
        throw new ParseException("Unsupported or invalid date format. Combined parse errors: " + parseErrors.toString(), 0);
    }
}
