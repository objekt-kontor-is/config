package de.objektkontor.config.valueparser;

public class BooleanValueParser extends AbtrsactArrayParser<Boolean> {

    @Override
    public Boolean parseValue(String value, Class<Boolean> resultType) {
        if ("true".equalsIgnoreCase(value))
            return true;
        if ("false".equalsIgnoreCase(value))
            return false;
        throw new IllegalArgumentException("Supported values are 'true' or 'false'");
    }
}
