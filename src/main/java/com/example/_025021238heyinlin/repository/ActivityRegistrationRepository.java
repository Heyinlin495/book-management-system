package com.example._025021238heyinlin.repository;

import com.example._025021238heyinlin.entity.ActivityRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Long> {
    List<ActivityRegistration> findByActivityIdOrderByRegistrationTimeDesc(Long activityId);
    List<ActivityRegistration> findByUserIdOrderByRegistrationTimeDesc(Long userId);
    List<ActivityRegistration> findByUserIdAndStatus(Long userId, String status);
    List<ActivityRegistration> findByUserIdAndIsCollectedTrue(Long userId);
    // 修改为返回最新的一条记录，避免多条记录报错
    Optional<ActivityRegistration> findFirstByActivityIdAndUserIdOrderByIdDesc(Long activityId, Long userId);
    // 获取所有记录（用于清理重复数据）
    List<ActivityRegistration> findByActivityIdAndUserId(Long activityId, Long userId);
    Long countByActivityIdAndStatus(Long activityId, String status);
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);
    // 检查用户是否已报名（仅检查 REGISTERED 状态）
    boolean existsByActivityIdAndUserIdAndStatus(Long activityId, Long userId, String status);
}
