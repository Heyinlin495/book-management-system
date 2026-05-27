package com.example._025021238heyinlin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "registration_time")
    private LocalDateTime registrationTime;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(length = 20)
    private String status; // REGISTERED, CHECKED_IN, CANCELLED

    @Column(name = "is_collected")
    private Boolean isCollected;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        registrationTime = LocalDateTime.now();
        if (status == null) status = "REGISTERED";
        if (isCollected == null) isCollected = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
