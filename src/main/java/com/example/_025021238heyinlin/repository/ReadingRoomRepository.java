package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.ReadingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReadingRoomRepository extends JpaRepository<ReadingRoom, Long> {
    List<ReadingRoom> findByIsActiveTrueOrderByNameAsc();
    boolean existsByName(String name);
}
