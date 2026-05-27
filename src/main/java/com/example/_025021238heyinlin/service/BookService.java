package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.BookDTO;
import com.example._025021238heyinlin.entity.Book;
import com.example._025021238heyinlin.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        log.info("获取所有图书");
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookById(Long id) {
        log.info("获取图书，ID: {}", id);
        return bookRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchByTitle(String title) {
        log.info("按标题搜索图书: {}", title);
        return bookRepository.findByTitleContaining(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchByAuthor(String author) {
        log.info("按作者搜索图书: {}", author);
        return bookRepository.findByAuthorContaining(author).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchByCategory(String category) {
        log.info("按分类搜索图书: {}", category);
        return bookRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        log.info("创建新图书: {}", bookDTO.getTitle());
        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        log.info("更新图书，ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书未找到"));
        
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishDate(bookDTO.getPublishDate());
        book.setPrice(bookDTO.getPrice());
        book.setDescription(bookDTO.getDescription());
        book.setCategory(bookDTO.getCategory());
        book.setStockQuantity(bookDTO.getStockQuantity());
        
        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        log.info("删除图书，ID: {}", id);
        bookRepository.deleteById(id);
    }

    @Transactional
    public BookDTO updateStock(Long id, Integer quantity) {
        log.info("更新库存，ID: {}, 数量: {}", id, quantity);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书未找到"));
        book.setStockQuantity(quantity);
        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    private BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .price(book.getPrice())
                .description(book.getDescription())
                .category(book.getCategory())
                .stockQuantity(book.getStockQuantity())
                .build();
    }

    private Book convertToEntity(BookDTO bookDTO) {
        return Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .isbn(bookDTO.getIsbn())
                .publisher(bookDTO.getPublisher())
                .publishDate(bookDTO.getPublishDate())
                .price(bookDTO.getPrice())
                .description(bookDTO.getDescription())
                .category(bookDTO.getCategory())
                .stockQuantity(bookDTO.getStockQuantity())
                .build();
    }
}
