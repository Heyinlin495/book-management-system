package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.ForumSectionDTO;
import com.example._025021238heyinlin.dto.PostDTO;
import com.example._025021238heyinlin.dto.CommentDTO;
import com.example._025021238heyinlin.jwt.UserDetailsImpl;
import com.example._025021238heyinlin.service.ForumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ForumController {

    private final ForumService forumService;

    // ========== 版块接口 ==========
    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<ForumSectionDTO>>> getAllSections() {
        return ResponseEntity.ok(ApiResponse.success(forumService.getAllSections()));
    }

    @GetMapping("/sections/active")
    public ResponseEntity<ApiResponse<List<ForumSectionDTO>>> getActiveSections() {
        return ResponseEntity.ok(ApiResponse.success(forumService.getActiveSections()));
    }

    @PostMapping("/sections")
    public ResponseEntity<ApiResponse<ForumSectionDTO>> createSection(@RequestBody ForumSectionDTO dto) {
        try {
            ForumSectionDTO created = forumService.createSection(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("版块创建成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<ForumSectionDTO>> updateSection(@PathVariable Long id, @RequestBody ForumSectionDTO dto) {
        try {
            ForumSectionDTO updated = forumService.updateSection(id, dto);
            return ResponseEntity.ok(ApiResponse.success("版块更新成功", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long id) {
        forumService.deleteSection(id);
        return ResponseEntity.ok(ApiResponse.success("版块删除成功", null));
    }

    // ========== 帖子接口 ==========
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getAllPosts(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId != null) {
            return ResponseEntity.ok(ApiResponse.success(forumService.getAllPostsWithUserStatus(userId)));
        }
        return ResponseEntity.ok(ApiResponse.success(forumService.getAllPosts()));
    }

    @GetMapping("/posts/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getPostsBySection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(ApiResponse.success(forumService.getPostsBySection(sectionId)));
    }

    @GetMapping("/posts/me")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getMyPosts(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(forumService.getMyPosts(userId)));
    }

    @GetMapping("/posts/hot")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getHotPosts() {
        return ResponseEntity.ok(ApiResponse.success(forumService.getHotPosts()));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(
            @PathVariable Long id,
            Authentication authentication) {
        return forumService.getPostByIdWithUserStatus(id, getCurrentUserId(authentication))
                .map(post -> ResponseEntity.ok(ApiResponse.success(post)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "帖子未找到")));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostDTO>> createPost(@RequestBody PostDTO dto, Authentication authentication) {
        dto.setUserId(((UserDetailsImpl) authentication.getPrincipal()).getId());
        try {
            PostDTO created = forumService.createPost(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("帖子发布成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> updatePost(@PathVariable Long id, @RequestBody PostDTO dto) {
        try {
            PostDTO updated = forumService.updatePost(id, dto);
            return ResponseEntity.ok(ApiResponse.success("帖子更新成功", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        try {
            forumService.deletePost(id);
            return ResponseEntity.ok(ApiResponse.success("帖子删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    // ========== 评论接口 ==========
    @GetMapping("/comments/post/{postId}")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success(forumService.getCommentsByPost(postId)));
    }

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(@RequestBody CommentDTO dto, Authentication authentication) {
        dto.setUserId(((UserDetailsImpl) authentication.getPrincipal()).getId());
        try {
            CommentDTO created = forumService.createComment(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("评论发布成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        try {
            forumService.deleteComment(id);
            return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    // ========== 点赞接口 ==========
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        try {
            boolean liked = forumService.toggleLike(id, userId);
            String message = liked ? "点赞成功" : "取消点赞";
            return ResponseEntity.ok(ApiResponse.success(message, liked));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> checkLiked(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(forumService.isLiked(id, userId)));
    }

    // ========== 收藏接口 ==========
    @PostMapping("/posts/{id}/favorite")
    public ResponseEntity<ApiResponse<Boolean>> toggleFavorite(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        try {
            boolean favorited = forumService.toggleFavorite(id, userId);
            String message = favorited ? "收藏成功" : "取消收藏";
            return ResponseEntity.ok(ApiResponse.success(message, favorited));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/posts/{id}/favorite")
    public ResponseEntity<ApiResponse<Boolean>> checkFavorited(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(forumService.isFavorited(id, userId)));
    }

    @GetMapping("/posts/favorites/me")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getMyFavorites(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(forumService.getMyFavorites(userId)));
    }

    // ========== 搜索接口 ==========
    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<List<PostDTO>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long sectionId) {
        if (sectionId != null) {
            return ResponseEntity.ok(ApiResponse.success(forumService.searchPostsInSection(keyword, sectionId)));
        }
        return ResponseEntity.ok(ApiResponse.success(forumService.searchPosts(keyword)));
    }

    // ========== 排序接口 ==========
    @GetMapping("/posts/latest")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getLatestPosts() {
        return ResponseEntity.ok(ApiResponse.success(forumService.getLatestPosts()));
    }

    @GetMapping("/posts/likes")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getPostsByLikes() {
        return ResponseEntity.ok(ApiResponse.success(forumService.getPostsSortedByLikes()));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return null;
    }
}
