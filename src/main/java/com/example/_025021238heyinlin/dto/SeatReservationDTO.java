package com.example._025021238heyinlin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatReservationDTO {
    private Long id;
    private Long seatId;
    private String seatNumber;
    private Long roomId;
    private String roomName;
    private Long userId;
    private String username;
    private LocalDate reservationDate;
    private String startTime;
    private String endTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
}
