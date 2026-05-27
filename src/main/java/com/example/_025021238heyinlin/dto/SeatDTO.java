package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDTO {
    private Long id;
    private Long roomId;
    private String roomName;
    private String seatNumber;
    private Integer rowNumber;
    private Integer columnNumber;
    private Boolean hasPower;
    private Boolean nearWindow;
    private String status;
    private Boolean isActive;
}
