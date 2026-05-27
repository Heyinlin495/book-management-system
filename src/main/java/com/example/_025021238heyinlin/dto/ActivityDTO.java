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
public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime registrationDeadline;
    private String status;
    private Boolean isHot;
    private Boolean isRegistered;
    private Boolean isCollected;
    private LocalDateTime createdAt;
}
