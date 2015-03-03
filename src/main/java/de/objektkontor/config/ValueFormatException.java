package de.objektkontor.config;

import static java.lang.String.format;

public class ValueFormatException extends RuntimeException {

    private static final long serialVersionUID = 253881058471256863L;

    private final String bundle;
    private final String key;

    public ValueFormatException(String bundle, String key, String message) {
        super(message);
        this.bundle = bundle;
        this.key = key;
    }

    public ValueFormatException(String bundle, String key, Throwable cause) {
        super(cause);
        this.bundle = bundle;
        this.key = key;
    }

    @Override
    public String getMessage() {
        return format("Invalid value for key '%s' in bundle '%s': " + super.getMessage(), bundle, key);
    }
}