package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.ForumSectionDTO;
import com.example._025021238heyinlin.dto.PostDTO;
import com.example._025021238heyinlin.dto.CommentDTO;
import com.example._025021238heyinlin.entity.ForumSection;
import com.example._025021238heyinlin.entity.Post;
import com.example._025021238heyinlin.entity.Comment;
import com.example._025021238heyinlin.entity.User;
import com.example._025021238heyinlin.entity.PostLike;
import com.example._025021238heyinlin.entity.PostFavorite;
import com.example._025021238heyinlin.repository.ForumSectionRepository;
import com.example._025021238heyinlin.repository.PostRepository;
import com.example._025021238heyinlin.repository.CommentRepository;
import com.example._025021238heyinlin.repository.UserRepository;
import com.example._025021238heyinlin.repository.PostLikeRepository;
import com.example._025021238heyinlin.repository.PostFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumService {

    private final ForumSectionRepository sectionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostFavoriteRepository postFavoriteRepository;

    // ========== 版块管理 ==========
    @Transactional(readOnly = true)
    public List<ForumSectionDTO> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::convertSectionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumSectionDTO> getActiveSections() {
        return sectionRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::convertSectionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ForumSectionDTO createSection(ForumSectionDTO dto) {
        log.info("创建社区版块: {}", dto.getName());
        ForumSection section = ForumSection.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .sortOrder(dto.getSortOrder())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        return convertSectionToDTO(sectionRepository.save(section));
    }

    @Transactional
    public ForumSectionDTO updateSection(Long id, ForumSectionDTO dto) {
        ForumSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("版块未找到"));
        section.setName(dto.getName());
        section.setDescription(dto.getDescription());
        section.setIcon(dto.getIcon());
        section.setSortOrder(dto.getSortOrder());
        if (dto.getIsActive() != null) section.setIsActive(dto.getIsActive());
        return convertSectionToDTO(sectionRepository.save(section));
    }

    @Transactional
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    // ========== 帖子管理 ==========
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        return postRepository.findByStatusOrderByCreatedAtDesc("PUBLISHED").stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getPostsBySection(Long sectionId) {
        return postRepository.findBySectionIdAndStatusOrderByCreatedAtDesc(sectionId, "PUBLISHED").stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getMyPosts(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getHotPosts() {
        return postRepository.findHotPosts().stream()
                .limit(10)
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<PostDTO> getPostById(Long id) {
        return postRepository.findById(id).map(post -> {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
            return convertPostToDTO(post);
        });
    }

    @Transactional
    public PostDTO createPost(PostDTO dto) {
        log.info("创建帖子: {}", dto.getTitle());
        ForumSection section = sectionRepository.findById(dto.getSectionId())
                .orElseThrow(() -> new RuntimeException("版块未找到"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .section(section)
                .user(user)
                .build();
        
        section.setPostCount(section.getPostCount() + 1);
        sectionRepository.save(section);
        
        return convertPostToDTO(postRepository.save(post));
    }

    @Transactional
    public PostDTO updatePost(Long id, PostDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        if (dto.getIsTop() != null) post.setIsTop(dto.getIsTop());
        if (dto.getIsHot() != null) post.setIsHot(dto.getIsHot());
        if (dto.getStatus() != null) post.setStatus(dto.getStatus());
        return convertPostToDTO(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        ForumSection section = post.getSection();
        section.setPostCount(Math.max(0, section.getPostCount() - 1));
        sectionRepository.save(section);
        postRepository.deleteById(id);
    }

    // ========== 评论管理 ==========
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdAndStatusOrderByCreatedAtAsc(postId, "PUBLISHED").stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createComment(CommentDTO dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .post(post)
                .user(user)
                .parentId(dto.getParentId())
                .build();
        
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        
        return convertCommentToDTO(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论未找到"));
        Post post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
        commentRepository.deleteById(id);
    }

    // ========== 转换方法 ==========
    private ForumSectionDTO convertSectionToDTO(ForumSection section) {
        return ForumSectionDTO.builder()
                .id(section.getId())
                .name(section.getName())
                .description(section.getDescription())
                .icon(section.getIcon())
                .sortOrder(section.getSortOrder())
                .isActive(section.getIsActive())
                .postCount(section.getPostCount())
                .build();
    }

    private PostDTO convertPostToDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .sectionId(post.getSection().getId())
                .sectionName(post.getSection().getName())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .userAvatar(post.getUser().getAvatar())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .isTop(post.getIsTop())
                .isHot(post.getIsHot())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .userAvatar(comment.getUser().getAvatar())
                .parentId(comment.getParentId())
                .likeCount(comment.getLikeCount())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    // ========== 点赞功能 ==========
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            return false; // 取消点赞
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return true; // 点赞成功
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    // ========== 收藏功能 ==========
    @Transactional
    public boolean toggleFavorite(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        Optional<PostFavorite> existingFavorite = postFavoriteRepository.findByPostIdAndUserId(postId, userId);
        if (existingFavorite.isPresent()) {
            postFavoriteRepository.delete(existingFavorite.get());
            return false; // 取消收藏
        } else {
            PostFavorite favorite = PostFavorite.builder()
                    .post(post)
                    .user(user)
                    .build();
            postFavoriteRepository.save(favorite);
            return true; // 收藏成功
        }
    }

    @Transactional(readOnly = true)
    public boolean isFavorited(Long postId, Long userId) {
        return postFavoriteRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getMyFavorites(Long userId) {
        return postFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(fav -> convertPostToDTO(fav.getPost()))
                .collect(Collectors.toList());
    }

    // ========== 搜索功能 ==========
    @Transactional(readOnly = true)
    public List<PostDTO> searchPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPosts();
        }
        return postRepository.searchByKeyword(keyword.trim()).stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> searchPostsInSection(String keyword, Long sectionId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPostsBySection(sectionId);
        }
        return postRepository.searchByKeywordInSection(keyword.trim(), sectionId).stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    // ========== 排序功能 ==========
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsSortedByLikes() {
        return postRepository.findByLikeCountDesc().stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getLatestPosts() {
        return postRepository.findLatestPosts().stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }

    // ========== 带用户状态的帖子查询 ==========
    @Transactional
    public Optional<PostDTO> getPostByIdWithUserStatus(Long id, Long userId) {
        return postRepository.findById(id).map(post -> {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
            PostDTO dto = convertPostToDTO(post);
            if (userId != null) {
                dto.setIsLiked(postLikeRepository.existsByPostIdAndUserId(id, userId));
                dto.setIsFavorited(postFavoriteRepository.existsByPostIdAndUserId(id, userId));
            }
            return dto;
        });
    }

    // ========== 带用户状态的帖子列表 ==========
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPostsWithUserStatus(Long userId) {
        return postRepository.findByStatusOrderByCreatedAtDesc("PUBLISHED").stream()
                .map(post -> {
                    PostDTO dto = convertPostToDTO(post);
                    if (userId != null) {
                        dto.setIsLiked(postLikeRepository.existsByPostIdAndUserId(post.getId(), userId));
                        dto.setIsFavorited(postFavoriteRepository.existsByPostIdAndUserId(post.getId(), userId));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
