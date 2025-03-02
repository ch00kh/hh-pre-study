package site.ch00kh.domain.post.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.ch00kh.domain.account.dto.AccountDetails;
import site.ch00kh.domain.post.dao.Post;
import site.ch00kh.domain.post.dao.PostRepository;
import site.ch00kh.domain.post.dto.PostDeleteRequestDto;
import site.ch00kh.domain.post.dto.PostPageRequestDto;
import site.ch00kh.domain.post.dto.PostWriteRequestDto;
import site.ch00kh.domain.post.dto.PostWriteResponseDto;
import site.ch00kh.domain.post.exception.PostNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public PostWriteResponseDto read(Long id) {

        Post post = postRepository.findByIdAndIsDeleted(id, "N").orElseThrow(PostNotFoundException::new);
        return PostWriteResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostWriteResponseDto> readAll(PostPageRequestDto postPageRequestDto) {

        Pageable pageable = postPageRequestDto.toPageable();
        Page<Post> postPage = postRepository.findByIsDeleted("N", pageable);

        return postPage.map(PostWriteResponseDto::from);
    }

    public PostWriteResponseDto write(PostWriteRequestDto postWriteDto, AccountDetails accountDetails) {

        String encodedPassword = bCryptPasswordEncoder.encode(postWriteDto.getPassword());
        Post requestPost = postWriteDto.toPost(postWriteDto, accountDetails.getUsername(), encodedPassword);
        Post savedPost = postRepository.save(requestPost);

        return PostWriteResponseDto.from(savedPost);
    }

    public PostWriteResponseDto modify(Long id, PostWriteRequestDto requestDto, AccountDetails accountDetails) throws BadRequestException {

        String password = requestDto.getPassword();
        String username = accountDetails.getUsername();

        Post validPost = isValid(id, password, username);
        Post modifiedPost = validPost.modify(requestDto);

        return PostWriteResponseDto.from(modifiedPost);
    }

    public void delete(Long id, PostDeleteRequestDto requestDto, AccountDetails accountDetails) throws BadRequestException {

        String password = requestDto.getPassword();
        String username = accountDetails.getUsername();

        Post validPost = isValid(id, password, username);

        if (validPost.getIsDeleted().equalsIgnoreCase("Y")) {
            throw new PostNotFoundException();
        }

        validPost.delete();
    }

    private Post isValid(Long id, String password, String username) throws BadRequestException {
        Post post = postRepository.findById(id).orElseThrow(RuntimeException::new);

        if (!bCryptPasswordEncoder.matches(password, post.getPassword())) {
            throw new BadRequestException();
        }

        if (!post.getWriter().equals(username)) {
            throw new BadRequestException();
        }

        return post;
    }
}
