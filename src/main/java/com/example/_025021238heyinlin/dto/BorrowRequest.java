package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest {
    private Long userId;
    private Long bookId;
    private Integer borrowDays;
    private String notes;
}
