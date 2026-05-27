package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingRoomDTO {
    private Long id;
    private String name;
    private String location;
    private String description;
    private Integer totalSeats;
    private Integer availableSeats;
    private String openTime;
    private String closeTime;
    private Boolean isActive;
}
