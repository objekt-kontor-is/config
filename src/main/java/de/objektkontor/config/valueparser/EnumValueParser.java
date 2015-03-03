package de.objektkontor.config.valueparser;


public class EnumValueParser<T extends Enum<T>> extends AbtrsactArrayParser<T> {

    @Override
    public T parseValue(String value, Class<T> resultType) {
        return Enum.valueOf(resultType, value);
    }
}
