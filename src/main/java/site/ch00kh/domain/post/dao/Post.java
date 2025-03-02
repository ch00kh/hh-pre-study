package site.ch00kh.domain.post.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.ch00kh.domain.post.dto.PostWriteRequestDto;
import site.ch00kh.domain.post.dto.PostWriteResponseDto;
import site.ch00kh.global.common.BaseEntity;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(length = 100)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String writer;

    @Column(length = 1)
    private String isDeleted;

    private String password;

    public Post modify(PostWriteRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();

        return this;
    }

    public void delete() {
        this.isDeleted = "Y";
    }

}
