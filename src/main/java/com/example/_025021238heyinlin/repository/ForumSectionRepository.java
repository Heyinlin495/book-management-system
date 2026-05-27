package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.ForumSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumSectionRepository extends JpaRepository<ForumSection, Long> {
    List<ForumSection> findByIsActiveTrueOrderBySortOrderAsc();
    boolean existsByName(String name);
}
