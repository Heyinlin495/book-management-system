package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRankingDTO {
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCategory;
    private Long borrowCount;
}