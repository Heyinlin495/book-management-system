package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.dto.BookRankingDTO;
import com.example._025021238heyinlin.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);
    List<BorrowRecord> findByBookId(Long bookId);
    List<BorrowRecord> findByStatus(String status);
    Optional<BorrowRecord> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);
    
    @Modifying
    @Query("DELETE FROM BorrowRecord b WHERE b.user.id = :userId")
    void deleteByUserId(Long userId);
    
    @Query("SELECT new com.example._025021238heyinlin.dto.BookRankingDTO(b.book.id, b.book.title, b.book.author, b.book.category, COUNT(b)) " +
           "FROM BorrowRecord b " +
           "WHERE b.status IN ('RETURNED', 'BORROWED', 'OVERDUE') " +
           "GROUP BY b.book.id, b.book.title, b.book.author, b.book.category " +
           "ORDER BY COUNT(b) DESC")
    List<BookRankingDTO> findBookRanking();
}
