package de.objektkontor.config;

public class DuplicateConfigIdException extends RuntimeException {

    private static final long serialVersionUID = -1148259007096212825L;

    public DuplicateConfigIdException() {
    }

    public DuplicateConfigIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DuplicateConfigIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateConfigIdException(String message) {
        super(message);
    }

    public DuplicateConfigIdException(Throwable cause) {
        super(cause);
    }
}
