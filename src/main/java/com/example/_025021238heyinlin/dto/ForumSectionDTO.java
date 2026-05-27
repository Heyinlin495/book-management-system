package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumSectionDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Boolean isActive;
    private Integer postCount;
}
