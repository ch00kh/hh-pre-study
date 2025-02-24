package site.ch00kh.domain.account.api;

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
import site.ch00kh.global.common.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;



    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<JoinResponseDto>> signup(@Valid @RequestBody JoinRequestDto dto) throws BadRequestException {

        JoinResponseDto joinResponseDto = accountService.signup(dto);
        return ApiResponse.ok("회원가입을 완료했습니다.", joinResponseDto);
    }

}
