package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.ReadingRoomDTO;
import com.example._025021238heyinlin.dto.SeatDTO;
import com.example._025021238heyinlin.dto.SeatReservationDTO;
import com.example._025021238heyinlin.jwt.UserDetailsImpl;
import com.example._025021238heyinlin.service.ReadingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reading-rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ReadingRoomController {

    private final ReadingRoomService readingRoomService;

    // ========== 阅览室接口 ==========
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadingRoomDTO>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success(readingRoomService.getAllRooms()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ReadingRoomDTO>>> getActiveRooms() {
        return ResponseEntity.ok(ApiResponse.success(readingRoomService.getActiveRooms()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingRoomDTO>> getRoomById(@PathVariable Long id) {
        return readingRoomService.getRoomById(id)
                .map(room -> ResponseEntity.ok(ApiResponse.success(room)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "阅览室未找到")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReadingRoomDTO>> createRoom(@RequestBody ReadingRoomDTO dto) {
        try {
            ReadingRoomDTO created = readingRoomService.createRoom(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("阅览室创建成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingRoomDTO>> updateRoom(@PathVariable Long id, @RequestBody ReadingRoomDTO dto) {
        try {
            ReadingRoomDTO updated = readingRoomService.updateRoom(id, dto);
            return ResponseEntity.ok(ApiResponse.success("阅览室更新成功", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        try {
            readingRoomService.deleteRoom(id);
            return ResponseEntity.ok(ApiResponse.success("阅览室删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    // ========== 座位接口 ==========
    @GetMapping("/{roomId}/seats")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getSeatsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(readingRoomService.getSeatsByRoom(roomId)));
    }

    @GetMapping("/{roomId}/seats/available")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getAvailableSeatsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(readingRoomService.getAvailableSeatsByRoom(roomId)));
    }

    @PostMapping("/seats")
    public ResponseEntity<ApiResponse<SeatDTO>> createSeat(@RequestBody SeatDTO dto) {
        try {
            SeatDTO created = readingRoomService.createSeat(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("座位创建成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/seats/{id}")
    public ResponseEntity<ApiResponse<SeatDTO>> updateSeat(@PathVariable Long id, @RequestBody SeatDTO dto) {
        try {
            SeatDTO updated = readingRoomService.updateSeat(id, dto);
            return ResponseEntity.ok(ApiResponse.success("座位更新成功", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/seats/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(@PathVariable Long id) {
        try {
            readingRoomService.deleteSeat(id);
            return ResponseEntity.ok(ApiResponse.success("座位删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    // ========== 座位预约接口 ==========
    @GetMapping("/reservations/me")
    public ResponseEntity<ApiResponse<List<SeatReservationDTO>>> getMyReservations(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(readingRoomService.getMyReservations(userId)));
    }

    @GetMapping("/{roomId}/reservations")
    public ResponseEntity<ApiResponse<List<SeatReservationDTO>>> getReservationsByRoomAndDate(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(readingRoomService.getReservationsByRoomAndDate(roomId, date)));
    }

    @PostMapping("/seats/{seatId}/reserve")
    public ResponseEntity<ApiResponse<SeatReservationDTO>> reserveSeat(
            @PathVariable Long seatId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        try {
            SeatReservationDTO reservation = readingRoomService.reserveSeat(seatId, userId, date, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("座位预约成功", reservation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/reservations/{reservationId}/checkin")
    public ResponseEntity<ApiResponse<SeatReservationDTO>> checkInSeat(@PathVariable Long reservationId) {
        try {
            SeatReservationDTO reservation = readingRoomService.checkInSeat(reservationId);
            return ResponseEntity.ok(ApiResponse.success("签到成功", reservation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/reservations/{reservationId}/checkout")
    public ResponseEntity<ApiResponse<SeatReservationDTO>> checkOutSeat(@PathVariable Long reservationId) {
        try {
            SeatReservationDTO reservation = readingRoomService.checkOutSeat(reservationId);
            return ResponseEntity.ok(ApiResponse.success("归还座位成功", reservation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long reservationId) {
        try {
            readingRoomService.cancelReservation(reservationId);
            return ResponseEntity.ok(ApiResponse.success("取消预约成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
