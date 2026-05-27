package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.CategoryDTO;
import com.example._025021238heyinlin.entity.Category;
import com.example._025021238heyinlin.repository.CategoryRepository;
import com.example._025021238heyinlin.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.info("获取所有图书分类");
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getActiveCategories() {
        log.info("获取所有活跃图书分类");
        return categoryRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(Long id) {
        log.info("获取图书分类，ID: {}", id);
        return categoryRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        log.info("创建图书分类: {}", dto.getName());
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .sortOrder(dto.getSortOrder())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        log.info("更新图书分类，ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类未找到"));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIcon(dto.getIcon());
        category.setSortOrder(dto.getSortOrder());
        if (dto.getIsActive() != null) {
            category.setIsActive(dto.getIsActive());
        }
        Category updated = categoryRepository.save(category);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("删除图书分类，ID: {}", id);
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .sortOrder(category.getSortOrder())
                .isActive(category.getIsActive())
                .bookCount(0) // 可以后续添加统计
                .build();
    }
}
