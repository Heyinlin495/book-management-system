package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();
    boolean existsByName(String name);
}
