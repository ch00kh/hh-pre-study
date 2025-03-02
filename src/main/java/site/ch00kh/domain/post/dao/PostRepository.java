package site.ch00kh.domain.post.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndIsDeleted(Long id, String isDeleted);

    Page<Post> findByIsDeleted(String isDeleted, Pageable pageable);

}
