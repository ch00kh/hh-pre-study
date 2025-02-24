package site.ch00kh.domain.account.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.ch00kh.domain.account.application.AccountService;
import site.ch00kh.domain.account.dto.JoinRequestDto;
import site.ch00kh.domain.account.dto.JoinResponseDto;
import site.ch00kh.domain.account.dto.JwtTokenRefreshDto;
import site.ch00kh.global.common.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody JoinRequestDto dto) throws BadRequestException {

        JoinResponseDto joinResponseDto = accountService.signup(dto);
        return ApiResponse.create("회원가입을 완료했습니다.", joinResponseDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        JwtTokenRefreshDto reissue = accountService.reissue(request, response);

        return reissue.getErrorMessage() == null
                ? ApiResponse.ok("token이 재발급되었습니다.", reissue)
                : ApiResponse.badRequest(reissue.getErrorMessage());
    }

}
