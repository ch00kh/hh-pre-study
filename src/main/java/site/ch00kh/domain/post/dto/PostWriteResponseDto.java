package site.ch00kh.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import site.ch00kh.domain.post.dao.Post;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostWriteResponseDto {

    private long id;
    private String writer;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isDeleted;

    public static PostWriteResponseDto from(Post post) {
        return PostWriteResponseDto.builder()
                .id(post.getId())
                .writer(post.getWriter())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .isDeleted(post.getIsDeleted().equals("Y") ? true : false)
                .build();
    }
}
