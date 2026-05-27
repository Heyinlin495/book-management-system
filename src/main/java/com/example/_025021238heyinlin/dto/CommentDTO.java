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
public class CommentDTO {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long parentId;
    private Integer likeCount;
    private String status;
    private LocalDateTime createdAt;
}
