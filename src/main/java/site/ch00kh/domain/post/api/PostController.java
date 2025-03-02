package site.ch00kh.domain.post.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.ch00kh.domain.account.dto.AccountDetails;
import site.ch00kh.domain.post.application.PostService;
import site.ch00kh.domain.post.dto.PostPageRequestDto;
import site.ch00kh.domain.post.dto.PostWriteRequestDto;
import site.ch00kh.domain.post.dto.PostWriteResponseDto;
import site.ch00kh.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {

        PostWriteResponseDto responseDto = postService.read(id);
        return ApiResponse.ok("게시글이 조회되었습니다.", responseDto);
    }

    @GetMapping
    public ResponseEntity<?> readAll(PostPageRequestDto postPageRequestDto) {

        Page<PostWriteResponseDto> postWriteResponseDtos = postService.readAll(postPageRequestDto);
        return ApiResponse.ok("게시글 목록이 조회되었습니다.", postWriteResponseDtos);
    }

    @PostMapping
    public ResponseEntity<?> write(@AuthenticationPrincipal AccountDetails accountDetails,
                                   @RequestBody PostWriteRequestDto requestDto) {

        PostWriteResponseDto responseDto = postService.write(requestDto, accountDetails);
        return ApiResponse.create("게시글이 작성되었습니다.", responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modify(@AuthenticationPrincipal AccountDetails accountDetails,
                                    @RequestBody PostWriteRequestDto requestDto,
                                    @PathVariable Long id) {

        PostWriteResponseDto responseDto = postService.modify(id, requestDto, accountDetails);
        return ApiResponse.ok("게시글이 수정되었습니다.",  responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal AccountDetails accountDetails,
                                    @RequestBody PostWriteRequestDto requestDto,
                                    @PathVariable Long id) {

        postService.delete(id, requestDto, accountDetails);
        return ApiResponse.ok("게시글이 삭제되었습니다.");
    }
}
