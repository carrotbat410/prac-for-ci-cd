package carrotbat410.lol.exhandler.exception;

public class RateExceededException extends RuntimeException {
    private String code;

    public RateExceededException() {
        super();
    }

    public RateExceededException(String message) {
        super(message);
    }

    public RateExceededException(String message, String code) {
        super(message);
        this.code = code;
    }

    public RateExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateExceededException(Throwable cause) {
        super(cause);
    }

    protected RateExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return this.code;
    }
}
