package carrotbat410.lol.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResult <T> {
    private final String message;
    private final T data;
    private final Long totalCnt;

    public SuccessResult() {
        this("ok");
    }

    public SuccessResult(String message) {
        this(message, null, null);
    }

    public SuccessResult(String message, T data) {
        this(message, data, null);
    }

    public SuccessResult(String message, T data, Long totalCnt) {
        this.message = message;
        this.data = data;
        this.totalCnt = totalCnt;
    }
}

