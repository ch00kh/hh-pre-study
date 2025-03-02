package site.ch00kh.domain.post.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import site.ch00kh.global.common.ApiResponse;

@Slf4j
@ControllerAdvice(basePackages = "site.ch00kh.domain.post.api")
public class PostExceptionHandler extends Exception {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        log.error("Username is Exist Error occurred at : [{}] ", e.getMessage());
        return ApiResponse.invalid("입력 값을 확인해주세요.");
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<?> handlePostNotFoundExceptions(PostNotFoundException e) {
        log.error("Post Not Found Error occurred at : [{}] ", e.getMessage());
        return ApiResponse.invalid("게시글을 찾을 수 없습니다.");
    }
}
