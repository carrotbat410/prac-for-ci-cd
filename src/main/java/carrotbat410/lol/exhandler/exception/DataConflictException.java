package carrotbat410.lol.exhandler.exception;

public class DataConflictException extends RuntimeException {
    private String code;

    public DataConflictException() {
        super();
    }

    public DataConflictException(String message) {
        super(message);
    }

    public DataConflictException(String message, String code) {
        super(message);
        this.code = code;
    }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataConflictException(Throwable cause) {
        super(cause);
    }

    protected DataConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return this.code;
    }
}
