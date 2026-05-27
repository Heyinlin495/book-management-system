package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.StatisticsDTO;
import com.example._025021238heyinlin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final PostRepository postRepository;
    private final ActivityRepository activityRepository;
    private final ReadingRoomRepository readingRoomRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public StatisticsDTO getStatistics() {
        log.info("获取系统统计数据");
        return StatisticsDTO.builder()
                .totalBooks(bookRepository.count())
                .totalUsers(userRepository.count())
                .totalBorrows(borrowRecordRepository.count())
                .activeBorrows(borrowRecordRepository.findByStatus("BORROWED").stream().count())
                .totalPosts(postRepository.count())
                .totalActivities(activityRepository.count())
                .totalReadingRooms(readingRoomRepository.count())
                .totalSeats(seatRepository.count())
                .occupiedSeats(seatRepository.findAll().stream()
                        .filter(s -> "OCCUPIED".equals(s.getStatus())).count())
                .build();
    }
}
