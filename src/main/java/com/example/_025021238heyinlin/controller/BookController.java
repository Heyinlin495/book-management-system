package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.BookDTO;
import com.example._025021238heyinlin.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        log.info("获取所有图书");
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookById(@PathVariable Long id) {
        log.info("获取图书，ID: {}", id);
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(ApiResponse.success(book)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "图书未找到")));
    }

    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchByTitle(@RequestParam String title) {
        log.info("按标题搜索: {}", title);
        List<BookDTO> books = bookService.searchByTitle(title);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchByAuthor(@RequestParam String author) {
        log.info("按作者搜索: {}", author);
        List<BookDTO> books = bookService.searchByAuthor(author);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/search/category")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchByCategory(@RequestParam String category) {
        log.info("按分类搜索: {}", category);
        List<BookDTO> books = bookService.searchByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@RequestBody BookDTO bookDTO) {
        log.info("创建新图书: {}", bookDTO.getTitle());
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("图书创建成功", createdBook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        log.info("更新图书，ID: {}", id);
        try {
            BookDTO updatedBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(ApiResponse.success("图书更新成功", updatedBook));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        log.info("删除图书，ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("图书删除成功", null));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<BookDTO>> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("更新库存，ID: {}, 数量: {}", id, quantity);
        try {
            BookDTO updatedBook = bookService.updateStock(id, quantity);
            return ResponseEntity.ok(ApiResponse.success("库存更新成功", updatedBook));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
}
