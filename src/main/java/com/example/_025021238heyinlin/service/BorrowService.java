package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.BookRankingDTO;
import com.example._025021238heyinlin.dto.BorrowRecordDTO;
import com.example._025021238heyinlin.dto.BorrowRequest;
import com.example._025021238heyinlin.entity.Book;
import com.example._025021238heyinlin.entity.BorrowRecord;
import com.example._025021238heyinlin.entity.User;
import com.example._025021238heyinlin.repository.BookRepository;
import com.example._025021238heyinlin.repository.BorrowRecordRepository;
import com.example._025021238heyinlin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BookRankingDTO> getBookRanking() {
        log.info("获取图书借阅排行榜");
        return borrowRecordRepository.findBookRanking();
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getAllBorrowRecords() {
        log.info("获取所有借阅记录");
        return borrowRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getUserBorrowRecords(Long userId) {
        log.info("获取用户借阅记录，用户ID: {}", userId);
        return borrowRecordRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getBookBorrowRecords(Long bookId) {
        log.info("获取图书借阅记录，图书ID: {}", bookId);
        return borrowRecordRepository.findByBookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getBorrowRecordsByStatus(String status) {
        log.info("获取借阅记录，状态: {}", status);
        return borrowRecordRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BorrowRecordDTO borrowBook(BorrowRequest request) {
        log.info("借书，用户ID: {}, 图书ID: {}", request.getUserId(), request.getBookId());

        // 检查用户是否存在
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查图书是否存在
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 检查库存
        if (book.getStockQuantity() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 检查用户是否已经借阅了该图书
        if (borrowRecordRepository.findByUserIdAndBookIdAndStatus(
                request.getUserId(), request.getBookId(), "BORROWED").isPresent()) {
            throw new RuntimeException("您已经借阅了该图书，请先归还后再借阅");
        }

        // 创建借阅记录
        LocalDateTime now = LocalDateTime.now();
        int borrowDays = request.getBorrowDays() != null ? request.getBorrowDays() : 30;
        LocalDateTime dueDate = now.plusDays(borrowDays);

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .user(user)
                .book(book)
                .borrowDate(now)
                .dueDate(dueDate)
                .status("BORROWED")
                .notes(request.getNotes())
                .build();

        // 更新图书库存
        book.setStockQuantity(book.getStockQuantity() - 1);
        bookRepository.save(book);

        // 保存借阅记录
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        log.info("借书成功，借阅记录ID: {}", savedRecord.getId());

        return convertToDTO(savedRecord);
    }

    @Transactional
    public BorrowRecordDTO returnBook(Long recordId) {
        log.info("还书，借阅记录ID: {}", recordId);

        BorrowRecord borrowRecord = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        if ("RETURNED".equals(borrowRecord.getStatus())) {
            throw new RuntimeException("该图书已归还");
        }

        // 更新借阅记录
        borrowRecord.setReturnDate(LocalDateTime.now());
        borrowRecord.setStatus("RETURNED");

        // 更新图书库存
        Book book = borrowRecord.getBook();
        book.setStockQuantity(book.getStockQuantity() + 1);
        bookRepository.save(book);

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
        log.info("还书成功，借阅记录ID: {}", recordId);

        return convertToDTO(updatedRecord);
    }

    @Transactional
    public void updateOverdueRecords() {
        log.info("更新逾期借阅记录");
        LocalDateTime now = LocalDateTime.now();
        List<BorrowRecord> borrowedRecords = borrowRecordRepository.findByStatus("BORROWED");

        for (BorrowRecord record : borrowedRecords) {
            if (record.getDueDate().isBefore(now)) {
                record.setStatus("OVERDUE");
                borrowRecordRepository.save(record);
            }
        }
    }

    private BorrowRecordDTO convertToDTO(BorrowRecord record) {
        return BorrowRecordDTO.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .username(record.getUser().getUsername())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .bookAuthor(record.getBook().getAuthor())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .notes(record.getNotes())
                .build();
    }
}
