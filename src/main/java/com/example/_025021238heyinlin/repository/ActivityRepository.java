package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStatusOrderByStartTimeAsc(String status);
    List<Activity> findByIsHotTrueOrderByStartTimeAsc();
    
    @Query("SELECT a FROM Activity a WHERE a.status = 'UPCOMING' AND a.startTime > :now ORDER BY a.startTime ASC")
    List<Activity> findUpcomingActivities(LocalDateTime now);
    
    @Query("SELECT a FROM Activity a WHERE a.status = 'ONGOING' ORDER BY a.startTime ASC")
    List<Activity> findOngoingActivities();
    
    @Query("SELECT a FROM Activity a ORDER BY a.createdAt DESC")
    List<Activity> findAllOrderByCreatedAtDesc();
}
