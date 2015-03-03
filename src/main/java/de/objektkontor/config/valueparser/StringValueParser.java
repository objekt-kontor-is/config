package de.objektkontor.config.valueparser;


public class StringValueParser extends AbtrsactArrayParser<String> {

    @Override
    public String parseValue(String value, Class<String> resultType) {
        return value;
    }
}
