package site.ch00kh.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtTokenRefreshDto {

    private String accessToken;
    private String refreshToken;
    private String errorMessage;

    public static JwtTokenRefreshDto error(String errorMessage) {
        return JwtTokenRefreshDto.builder()
                .errorMessage(errorMessage)
                .build();
    }

    public static JwtTokenRefreshDto success(String accessToken, String refreshToken) {
        return JwtTokenRefreshDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
