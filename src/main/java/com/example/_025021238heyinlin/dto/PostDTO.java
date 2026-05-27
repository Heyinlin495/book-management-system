package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Long sectionId;
    private String sectionName;
    private Long userId;
    private String username;
    private String userAvatar;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Boolean isTop;
    private Boolean isHot;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 用户交互状态
    private Boolean isLiked;
    private Boolean isFavorited;
}
