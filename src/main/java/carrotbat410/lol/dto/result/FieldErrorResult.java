package carrotbat410.lol.dto.result;

import lombok.Getter;

@Getter
public class FieldErrorResult {
    private final String code;
    private final String message;
    private final String fieldName;

    public FieldErrorResult(String code, String message) {
        this(code, message, null);
    }
    public FieldErrorResult(String code, String message, String fieldName) {
        this.code = code;
        this.message = message;
        this.fieldName = fieldName;
    }
}