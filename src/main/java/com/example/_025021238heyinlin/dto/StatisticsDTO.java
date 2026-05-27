package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    private Long totalBooks;
    private Long totalUsers;
    private Long totalBorrows;
    private Long activeBorrows;
    private Long totalPosts;
    private Long totalActivities;
    private Long totalReadingRooms;
    private Long totalSeats;
    private Long occupiedSeats;
}
