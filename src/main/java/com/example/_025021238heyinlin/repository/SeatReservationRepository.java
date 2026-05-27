package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    List<SeatReservation> findByUserIdOrderByReservationDateDesc(Long userId);
    List<SeatReservation> findByUserIdAndStatus(Long userId, String status);
    List<SeatReservation> findBySeatIdAndReservationDate(Long seatId, LocalDate date);
    Optional<SeatReservation> findBySeatIdAndReservationDateAndStatus(Long seatId, LocalDate date, String status);
    
    @Query("SELECT sr FROM SeatReservation sr WHERE sr.seat.room.id = :roomId AND sr.reservationDate = :date")
    List<SeatReservation> findByRoomIdAndDate(Long roomId, LocalDate date);
    
    boolean existsBySeatIdAndReservationDateAndStatusIn(Long seatId, LocalDate date, List<String> statuses);
    
    void deleteBySeatId(Long seatId);
}
