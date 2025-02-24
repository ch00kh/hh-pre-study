package site.ch00kh.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import site.ch00kh.global.common.ApiResponse;
import site.ch00kh.global.common.ResponseCode;

import java.io.IOException;

@RequiredArgsConstructor
public class FilterResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void error(HttpServletResponse response, HttpStatus status, ResponseCode code, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = new ApiResponse<>(code.name(), message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

     public static void success(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = new ApiResponse<>("SUCCESS", message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

}
