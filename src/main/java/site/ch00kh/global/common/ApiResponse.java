package site.ch00kh.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseEntity<ApiResponse<?>> ok(String message) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", message, data));
    }

    public static ResponseEntity<ApiResponse<?>> create(String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("CREATE", message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> create(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("CREATE", message));
    }

    public static ResponseEntity<ApiResponse<?>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("UNAUTHORIZED", message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message, T data) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("UNAUTHORIZED", message, data));
    }

    public static ResponseEntity<ApiResponse<?>> invalid(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("INVALID", message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> invalid(String message, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("INVALID", message, data));
    }

}
