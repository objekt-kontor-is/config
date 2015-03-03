package de.objektkontor.config.valueparser;


public class LongValueParser extends AbtrsactArrayParser<Long> {

    @Override
    public Long parseValue(String value, Class<Long> resultType) {
        return Long.parseLong(value);
    }
}
