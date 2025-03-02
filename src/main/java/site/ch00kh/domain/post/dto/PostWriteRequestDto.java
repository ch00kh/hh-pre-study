package site.ch00kh.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import site.ch00kh.domain.post.dao.Post;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostWriteRequestDto {

    private String title;
    private String content;
    private String password;

    public Post toPost(PostWriteRequestDto dto, String username, String password) {
        return Post.builder()
                .title(dto.title)
                .content(dto.content)
                .password(password)
                .writer(username)
                .isDeleted("N")
                .build();
    }
}
