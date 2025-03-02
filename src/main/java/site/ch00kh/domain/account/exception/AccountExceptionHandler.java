package site.ch00kh.domain.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import site.ch00kh.global.common.ApiResponse;

@Slf4j
@ControllerAdvice(basePackages = "site.ch00kh.domain.account.api")
public class AccountExceptionHandler extends Exception {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        log.error("Username is Exist Error occurred at : [{}] ", e.getMessage());
        return ApiResponse.invalid("이미 사용중인 계정입니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("Signup Request Error occurred at : [{}] ", e.getMessage());
        return ApiResponse.invalid("입력값을 확인해주세요.");
    }

}
