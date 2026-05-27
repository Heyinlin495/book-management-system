package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySectionIdAndStatusOrderByCreatedAtDesc(Long sectionId, String status);
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findByStatusOrderByCreatedAtDesc(String status);
    List<Post> findByIsTopTrueAndStatusOrderByCreatedAtDesc(String status);
    List<Post> findByIsHotTrueAndStatusOrderByCreatedAtDesc(String status);
    
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.viewCount DESC")
    List<Post> findHotPosts();
    
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.createdAt DESC")
    List<Post> findLatestPosts();
    
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.likeCount DESC")
    List<Post> findByLikeCountDesc();
    
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY p.createdAt DESC")
    List<Post> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.section.id = :sectionId AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY p.createdAt DESC")
    List<Post> searchByKeywordInSection(@Param("keyword") String keyword, @Param("sectionId") Long sectionId);
}
