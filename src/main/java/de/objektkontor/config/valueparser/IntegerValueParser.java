package de.objektkontor.config.valueparser;


public class IntegerValueParser extends AbtrsactArrayParser<Integer> {

    @Override
    public Integer parseValue(String value, Class<Integer> resultType) {
        return Integer.parseInt(value);
    }
}
