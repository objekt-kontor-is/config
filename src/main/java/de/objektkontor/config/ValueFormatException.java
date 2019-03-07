package de.objektkontor.config;

import static java.lang.String.format;

public class ValueFormatException extends RuntimeException {

    private static final long serialVersionUID = 253881058471256863L;

    private final String source;
    private final String key;

    public ValueFormatException(String source, String key, String message) {
        super(message);
        this.source = source;
        this.key = key;
    }

    public ValueFormatException(String source, String key, Throwable cause) {
        super(cause);
        this.source = source;
        this.key = key;
    }

    @Override
    public String getMessage() {
        return format("Invalid value for key '%s' in '%s': " + super.getMessage(), source, key);
    }
}