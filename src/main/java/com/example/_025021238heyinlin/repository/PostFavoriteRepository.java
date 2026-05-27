package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.PostFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {
    Optional<PostFavorite> findByPostIdAndUserId(Long postId, Long userId);
    List<PostFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
