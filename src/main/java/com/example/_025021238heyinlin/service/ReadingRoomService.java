package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.ReadingRoomDTO;
import com.example._025021238heyinlin.dto.SeatDTO;
import com.example._025021238heyinlin.dto.SeatReservationDTO;
import com.example._025021238heyinlin.entity.ReadingRoom;
import com.example._025021238heyinlin.entity.Seat;
import com.example._025021238heyinlin.entity.SeatReservation;
import com.example._025021238heyinlin.entity.User;
import com.example._025021238heyinlin.repository.ReadingRoomRepository;
import com.example._025021238heyinlin.repository.SeatRepository;
import com.example._025021238heyinlin.repository.SeatReservationRepository;
import com.example._025021238heyinlin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingRoomService {

    private final ReadingRoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final SeatReservationRepository reservationRepository;
    private final UserRepository userRepository;

    // ========== 阅览室管理 ==========
    @Transactional(readOnly = true)
    public List<ReadingRoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReadingRoomDTO> getActiveRooms() {
        return roomRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ReadingRoomDTO> getRoomById(Long id) {
        return roomRepository.findById(id).map(this::convertRoomToDTO);
    }

    @Transactional
    public ReadingRoomDTO createRoom(ReadingRoomDTO dto) {
        log.info("创建阅览室: {}", dto.getName());
        ReadingRoom room = ReadingRoom.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .totalSeats(dto.getTotalSeats())
                .availableSeats(dto.getTotalSeats())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        ReadingRoom saved = roomRepository.save(room);
        
        // 自动创建座位
        if (dto.getTotalSeats() != null && dto.getTotalSeats() > 0) {
            for (int i = 1; i <= dto.getTotalSeats(); i++) {
                Seat seat = Seat.builder()
                        .room(saved)
                        .seatNumber(String.format("%03d", i))
                        .rowNumber((i - 1) / 10 + 1)
                        .columnNumber((i - 1) % 10 + 1)
                        .build();
                seatRepository.save(seat);
            }
        }
        
        return convertRoomToDTO(saved);
    }

    @Transactional
    public ReadingRoomDTO updateRoom(Long id, ReadingRoomDTO dto) {
        ReadingRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("阅览室未找到"));
        room.setName(dto.getName());
        room.setLocation(dto.getLocation());
        room.setDescription(dto.getDescription());
        room.setOpenTime(dto.getOpenTime());
        room.setCloseTime(dto.getCloseTime());
        if (dto.getIsActive() != null) room.setIsActive(dto.getIsActive());
        return convertRoomToDTO(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        ReadingRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("阅览室未找到"));
        
        // 删除该阅览室的所有座位预约记录
        List<Seat> seats = seatRepository.findByRoomId(id);
        for (Seat seat : seats) {
            reservationRepository.deleteBySeatId(seat.getId());
        }
        
        // 删除该阅览室的所有座位
        seatRepository.deleteByRoomId(id);
        
        // 删除阅览室
        roomRepository.deleteById(id);
        log.info("删除阅览室: {}", room.getName());
    }

    // ========== 座位管理 ==========
    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByRoom(Long roomId) {
        return seatRepository.findByRoomIdAndIsActiveTrueOrderBySeatNumberAsc(roomId).stream()
                .map(this::convertSeatToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getAvailableSeatsByRoom(Long roomId) {
        List<Seat> seats = seatRepository.findByRoomIdAndIsActiveTrueOrderBySeatNumberAsc(roomId);
        Set<Long> reservedSeatIds = reservationRepository.findByRoomIdAndDate(roomId, LocalDate.now()).stream()
                .filter(reservation -> Arrays.asList("RESERVED", "CHECKED_IN").contains(reservation.getStatus()))
                .map(reservation -> reservation.getSeat().getId())
                .collect(Collectors.toSet());
        return seats.stream()
                .filter(seat -> !"MAINTENANCE".equals(seat.getStatus()))
                .filter(seat -> !reservedSeatIds.contains(seat.getId()))
                .map(this::convertSeatToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatDTO createSeat(SeatDTO dto) {
        ReadingRoom room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("阅览室未找到"));
        Seat seat = Seat.builder()
                .room(room)
                .seatNumber(dto.getSeatNumber())
                .rowNumber(dto.getRowNumber())
                .columnNumber(dto.getColumnNumber())
                .hasPower(dto.getHasPower())
                .nearWindow(dto.getNearWindow())
                .build();
        
        room.setTotalSeats(room.getTotalSeats() + 1);
        room.setAvailableSeats(room.getAvailableSeats() + 1);
        roomRepository.save(room);
        
        return convertSeatToDTO(seatRepository.save(seat));
    }

    @Transactional
    public SeatDTO updateSeat(Long id, SeatDTO dto) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("座位未找到"));
        seat.setSeatNumber(dto.getSeatNumber());
        seat.setRowNumber(dto.getRowNumber());
        seat.setColumnNumber(dto.getColumnNumber());
        if (dto.getHasPower() != null) seat.setHasPower(dto.getHasPower());
        if (dto.getNearWindow() != null) seat.setNearWindow(dto.getNearWindow());
        if (dto.getStatus() != null) seat.setStatus(dto.getStatus());
        if (dto.getIsActive() != null) seat.setIsActive(dto.getIsActive());
        return convertSeatToDTO(seatRepository.save(seat));
    }

    @Transactional
    public void deleteSeat(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("座位未找到"));
        ReadingRoom room = seat.getRoom();
        room.setTotalSeats(Math.max(0, room.getTotalSeats() - 1));
        if ("AVAILABLE".equals(seat.getStatus())) {
            room.setAvailableSeats(Math.max(0, room.getAvailableSeats() - 1));
        }
        roomRepository.save(room);
        seatRepository.deleteById(id);
    }

    // ========== 座位预约 ==========
    @Transactional(readOnly = true)
    public List<SeatReservationDTO> getMyReservations(Long userId) {
        return reservationRepository.findByUserIdOrderByReservationDateDesc(userId).stream()
                .map(this::convertReservationToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatReservationDTO> getReservationsByRoomAndDate(Long roomId, LocalDate date) {
        return reservationRepository.findByRoomIdAndDate(roomId, date).stream()
                .map(this::convertReservationToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatReservationDTO reserveSeat(Long seatId, Long userId, LocalDate date, String startTime, String endTime) {
        log.info("用户 {} 预约座位 {} 日期 {}", userId, seatId, date);

        List<String> activeStatuses = Arrays.asList("RESERVED", "CHECKED_IN");
        if (reservationRepository.existsBySeatIdAndReservationDateAndStatusIn(seatId, date, activeStatuses)) {
            throw new RuntimeException("该座位在该日期已被预约");
        }

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("座位未找到"));
        if (!Boolean.TRUE.equals(seat.getIsActive()) || "MAINTENANCE".equals(seat.getStatus())) {
            throw new RuntimeException("该座位当前不可预约");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        SeatReservation reservation = SeatReservation.builder()
                .seat(seat)
                .user(user)
                .reservationDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        return convertReservationToDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public SeatReservationDTO checkInSeat(Long reservationId) {
        SeatReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录未找到"));
        reservation.setCheckInTime(LocalDateTime.now());
        reservation.setStatus("CHECKED_IN");
        return convertReservationToDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public SeatReservationDTO checkOutSeat(Long reservationId) {
        SeatReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录未找到"));
        if (!"CHECKED_IN".equals(reservation.getStatus())) {
            throw new RuntimeException("只有已签到的预约才能签退");
        }
        reservation.setCheckOutTime(LocalDateTime.now());
        reservation.setStatus("COMPLETED");
        return convertReservationToDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        SeatReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录未找到"));

        if ("CHECKED_IN".equals(reservation.getStatus())) {
            throw new RuntimeException("已签到的预约不能取消");
        }
        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new RuntimeException("当前预约状态不能取消");
        }

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    // ========== 转换方法 ==========
    private ReadingRoomDTO convertRoomToDTO(ReadingRoom room) {
        long availableCount = seatRepository.findByRoomIdAndIsActiveTrueOrderBySeatNumberAsc(room.getId()).stream()
                .filter(seat -> !"MAINTENANCE".equals(seat.getStatus()))
                .count();
        return ReadingRoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .location(room.getLocation())
                .description(room.getDescription())
                .totalSeats(room.getTotalSeats())
                .availableSeats((int) availableCount)
                .openTime(room.getOpenTime())
                .closeTime(room.getCloseTime())
                .isActive(room.getIsActive())
                .build();
    }

    private SeatDTO convertSeatToDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .roomId(seat.getRoom().getId())
                .roomName(seat.getRoom().getName())
                .seatNumber(seat.getSeatNumber())
                .rowNumber(seat.getRowNumber())
                .columnNumber(seat.getColumnNumber())
                .hasPower(seat.getHasPower())
                .nearWindow(seat.getNearWindow())
                .status(seat.getStatus())
                .isActive(seat.getIsActive())
                .build();
    }

    private SeatReservationDTO convertReservationToDTO(SeatReservation res) {
        return SeatReservationDTO.builder()
                .id(res.getId())
                .seatId(res.getSeat().getId())
                .seatNumber(res.getSeat().getSeatNumber())
                .roomId(res.getSeat().getRoom().getId())
                .roomName(res.getSeat().getRoom().getName())
                .userId(res.getUser().getId())
                .username(res.getUser().getUsername())
                .reservationDate(res.getReservationDate())
                .startTime(res.getStartTime())
                .endTime(res.getEndTime())
                .checkInTime(res.getCheckInTime())
                .checkOutTime(res.getCheckOutTime())
                .status(res.getStatus())
                .build();
    }
}
