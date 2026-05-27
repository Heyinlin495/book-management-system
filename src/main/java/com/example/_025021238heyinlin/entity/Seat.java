package com.example._025021238heyinlin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ReadingRoom room;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "column_number")
    private Integer columnNumber;

    @Column(name = "has_power")
    private Boolean hasPower;

    @Column(name = "near_window")
    private Boolean nearWindow;

    @Column(length = 20)
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "AVAILABLE";
        if (isActive == null) isActive = true;
        if (hasPower == null) hasPower = false;
        if (nearWindow == null) nearWindow = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
