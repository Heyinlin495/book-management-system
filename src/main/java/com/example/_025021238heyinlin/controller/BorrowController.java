package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.BookRankingDTO;
import com.example._025021238heyinlin.dto.BorrowRecordDTO;
import com.example._025021238heyinlin.dto.BorrowRequest;
import com.example._025021238heyinlin.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example._025021238heyinlin.jwt.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class BorrowController {

    private final BorrowService borrowService;

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<BookRankingDTO>>> getBookRanking() {
        log.info("获取图书借阅排行榜");
        List<BookRankingDTO> ranking = borrowService.getBookRanking();
        return ResponseEntity.ok(ApiResponse.success(ranking));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getAllBorrowRecords() {
        log.info("获取所有借阅记录");
        List<BorrowRecordDTO> records = borrowService.getAllBorrowRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getUserBorrowRecords(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        log.info("获取当前用户借阅记录，用户ID: {}", userId);
        List<BorrowRecordDTO> records = borrowService.getUserBorrowRecords(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getBookBorrowRecords(@PathVariable Long bookId) {
        log.info("获取图书借阅记录，图书ID: {}", bookId);
        List<BorrowRecordDTO> records = borrowService.getBookBorrowRecords(bookId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getBorrowRecordsByStatus(@PathVariable String status) {
        log.info("获取借阅记录，状态: {}", status);
        List<BorrowRecordDTO> records = borrowService.getBorrowRecordsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BorrowRecordDTO>> borrowBook(@RequestBody BorrowRequest request,
                                                                   Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        request.setUserId(userId);
        log.info("借书请求，用户ID: {}, 图书ID: {}", request.getUserId(), request.getBookId());
        try {
            BorrowRecordDTO record = borrowService.borrowBook(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("借书成功", record));
        } catch (RuntimeException e) {
            log.error("借书失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{recordId}/return")
    public ResponseEntity<ApiResponse<BorrowRecordDTO>> returnBook(@PathVariable Long recordId) {
        log.info("还书请求，借阅记录ID: {}", recordId);
        try {
            BorrowRecordDTO record = borrowService.returnBook(recordId);
            return ResponseEntity.ok(ApiResponse.success("还书成功", record));
        } catch (RuntimeException e) {
            log.error("还书失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/update-overdue")
    public ResponseEntity<ApiResponse<Void>> updateOverdueRecords() {
        log.info("更新逾期借阅记录");
        borrowService.updateOverdueRecords();
        return ResponseEntity.ok(ApiResponse.success("逾期记录更新成功", null));
    }
}