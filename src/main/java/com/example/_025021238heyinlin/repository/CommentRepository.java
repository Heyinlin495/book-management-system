package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, String status);
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Comment> findByParentIdAndStatusOrderByCreatedAtAsc(Long parentId, String status);
    Long countByPostIdAndStatus(Long postId, String status);
}
