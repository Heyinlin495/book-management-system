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
public class ActivityRegistrationDTO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long userId;
    private String username;
    private LocalDateTime registrationTime;
    private LocalDateTime checkInTime;
    private String status;
    private Boolean isCollected;
}
