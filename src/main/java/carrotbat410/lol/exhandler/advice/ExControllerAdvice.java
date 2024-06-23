package carrotbat410.lol.exhandler.advice;

import carrotbat410.lol.dto.result.ErrorResult;
import carrotbat410.lol.dto.result.FieldErrorResult;
import carrotbat410.lol.exhandler.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackages = "carrotbat410.lol.controller")
public class ExControllerAdvice {

    //방법1
//    @ExceptionHandler
//    public ResponseEntity<ErrorResult> userExHandle(UserException e) {
//        log.error("[exceptionHandle] ex", e);
//        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
//    }

    //방법2
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult noBodyExHandle(HttpMessageNotReadableException e) {
        return new ErrorResult("BAD", "body값을 입력해주세요.");
    }


    //! 리턴타입 FieldErrorResult
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public FieldErrorResult beanValidationHandle(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] ex", e);

        FieldError error = ((FieldError) e.getBindingResult().getAllErrors().get(0));
        String fieldName = error.getField();
        String message = error.getDefaultMessage();
        String code = error.getCode();
//        log.info("fieldName: " + fieldName);
//        log.info("message: " + message);
//        log.info("code: " + code);

        return new FieldErrorResult(code, message, fieldName);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult pathVariableHandle(MethodArgumentTypeMismatchException e) {
        return new ErrorResult(e.getErrorCode(), "매개변수의 타입을 확인해주세요.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult notFoundHandle(NotFoundException e) {
        return new ErrorResult(null, e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResult accessDeniedHandle(AccessDeniedException e) {
        return new ErrorResult(null, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult conflictDataHandle(DataConflictException e) {
        return new ErrorResult(e.getCode(), e.getMessage());
    }

    //전체 예외 에러 처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        //TODO 텔레그램 메세지 추가하기?
        //TODO log.error메세지 저장하기?
        return new ErrorResult("EX", "내부 오류");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(JsonMappingException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("MAPPING", "Json Mapping Error");
    }


    // 외부 API 호출 관련 //TODO 특히, 텔레그램 메세지 알림 추가하기. (e.getStatusCode().value(); 해야 403, 409 숫자만 가져옴.)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler
    public ErrorResult rateExceededHandle(RateExceededException e) {
        log.error("외부 api 요청중 에러 발생", e);

        return new ErrorResult(null, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult apiErrorHandle(HttpClientErrorException e) {
        log.error("외부 api 요청중 에러 발생", e);

        //TODO 텔레그램 메세지 알림 추가하기. (e.getStatusCode().value(); 해야 403, 409 숫자만 가져옴.)
        return new ErrorResult(e.getStatusText(), "외부 API 호출 도중 에러 발생");
    }


}
