package carrotbat410.lol.exhandler.exception;

public class JsonMappingException extends RuntimeException {
    private String code;

    public JsonMappingException() {
        super();
    }

    public JsonMappingException(String message) {
        super(message);
    }

    public JsonMappingException(String message, String code) {
        super(message);
        this.code = code;
    }

    public JsonMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonMappingException(Throwable cause) {
        super(cause);
    }

    protected JsonMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return this.code;
    }
}
