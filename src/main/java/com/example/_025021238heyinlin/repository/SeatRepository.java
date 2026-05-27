package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomIdAndIsActiveTrueOrderBySeatNumberAsc(Long roomId);
    List<Seat> findByRoomIdAndStatusAndIsActiveTrueOrderBySeatNumberAsc(Long roomId, String status);
    Optional<Seat> findByRoomIdAndSeatNumber(Long roomId, String seatNumber);
    Long countByRoomIdAndStatusAndIsActiveTrue(Long roomId, String status);
    List<Seat> findByRoomId(Long roomId);
    void deleteByRoomId(Long roomId);
}
