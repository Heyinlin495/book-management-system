package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.CategoryDTO;
import com.example._025021238heyinlin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        log.info("获取所有图书分类");
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getActiveCategories() {
        log.info("获取活跃图书分类");
        return ResponseEntity.ok(ApiResponse.success(categoryService.getActiveCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(cat -> ResponseEntity.ok(ApiResponse.success(cat)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "分类未找到")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@RequestBody CategoryDTO dto) {
        log.info("创建图书分类: {}", dto.getName());
        try {
            CategoryDTO created = categoryService.createCategory(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("分类创建成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        try {
            CategoryDTO updated = categoryService.updateCategory(id, dto);
            return ResponseEntity.ok(ApiResponse.success("分类更新成功", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("分类删除成功", null));
    }
}
