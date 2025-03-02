package site.ch00kh.domain.post.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostPageRequestDto {

    private int page = 0;
    private int size = 10;
    private String keyword = "id";
    private String direction = "desc";

    public Pageable toPageable() {

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(keyword).ascending() :
                Sort.by(keyword).descending();

        return PageRequest.of(page, size, sort);
    }
}
