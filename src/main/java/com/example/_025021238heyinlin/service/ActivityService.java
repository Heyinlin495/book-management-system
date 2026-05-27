package com.example._025021238heyinlin.service;

import com.example._025021238heyinlin.dto.ActivityDTO;
import com.example._025021238heyinlin.dto.ActivityRegistrationDTO;
import com.example._025021238heyinlin.entity.Activity;
import com.example._025021238heyinlin.entity.ActivityRegistration;
import com.example._025021238heyinlin.entity.User;
import com.example._025021238heyinlin.repository.ActivityRepository;
import com.example._025021238heyinlin.repository.ActivityRegistrationRepository;
import com.example._025021238heyinlin.repository.UserRepository;
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
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    // ========== 活动管理 ==========
    @Transactional(readOnly = true)
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getUpcomingActivities() {
        return activityRepository.findUpcomingActivities(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getHotActivities() {
        return activityRepository.findByIsHotTrueOrderByStartTimeAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ActivityDTO> getActivityById(Long id) {
        return activityRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional
    public ActivityDTO getActivityByIdForUser(Long id, Long userId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动未找到"));
        ActivityDTO dto = convertToDTO(activity);
        if (userId != null) {
            // 清理重复数据并获取最新记录
            Optional<ActivityRegistration> reg = cleanupAndGetRegistration(id, userId);
            // 只有状态为 REGISTERED 的才算作已报名，CANCELLED 状态的不算
            dto.setIsRegistered(reg.filter(r -> "REGISTERED".equals(r.getStatus())).isPresent());
            dto.setIsCollected(reg.map(ActivityRegistration::getIsCollected).orElse(false));
        }
        return dto;
    }

    /**
     * 清理重复的报名记录，只保留最新的一条
     */
    @Transactional
    private Optional<ActivityRegistration> cleanupAndGetRegistration(Long activityId, Long userId) {
        List<ActivityRegistration> records = registrationRepository.findByActivityIdAndUserId(activityId, userId);
        if (records.isEmpty()) {
            return Optional.empty();
        }
        if (records.size() > 1) {
            log.warn("发现重复报名记录，活动ID: {}, 用户ID: {}, 数量: {}", activityId, userId, records.size());
            // 保留最新的一条，删除其他的
            ActivityRegistration latest = records.get(0);
            for (int i = 1; i < records.size(); i++) {
                registrationRepository.delete(records.get(i));
            }
            return Optional.of(latest);
        }
        return Optional.of(records.get(0));
    }

    @Transactional
    public ActivityDTO createActivity(ActivityDTO dto) {
        log.info("创建活动: {}", dto.getTitle());
        Activity activity = Activity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .coverImage(dto.getCoverImage())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
                .maxParticipants(dto.getMaxParticipants())
                .registrationDeadline(dto.getRegistrationDeadline())
                .status(dto.getStatus() != null ? dto.getStatus() : "UPCOMING")
                .isHot(dto.getIsHot() != null ? dto.getIsHot() : false)
                .build();
        return convertToDTO(activityRepository.save(activity));
    }

    @Transactional
    public ActivityDTO updateActivity(Long id, ActivityDTO dto) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动未找到"));
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setCoverImage(dto.getCoverImage());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLocation(dto.getLocation());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setRegistrationDeadline(dto.getRegistrationDeadline());
        if (dto.getStatus() != null) activity.setStatus(dto.getStatus());
        if (dto.getIsHot() != null) activity.setIsHot(dto.getIsHot());
        return convertToDTO(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    // ========== 活动报名 ==========
    @Transactional(readOnly = true)
    public List<ActivityRegistrationDTO> getRegistrationsByActivity(Long activityId) {
        return registrationRepository.findByActivityIdOrderByRegistrationTimeDesc(activityId).stream()
                .filter(r -> "REGISTERED".equals(r.getStatus()) || "CHECKED_IN".equals(r.getStatus()))
                .map(this::convertRegistrationToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityRegistrationDTO> getMyRegistrations(Long userId) {
        return registrationRepository.findByUserIdOrderByRegistrationTimeDesc(userId).stream()
                .map(this::convertRegistrationToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityRegistrationDTO> getMyCollectedActivities(Long userId) {
        return registrationRepository.findByUserIdAndIsCollectedTrue(userId).stream()
                .map(this::convertRegistrationToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityRegistrationDTO registerActivity(Long activityId, Long userId) {
        log.info("用户 {} 报名活动 {}", userId, activityId);
        
        // 清理重复数据并检查是否已有报名记录
        Optional<ActivityRegistration> existingReg = cleanupAndGetRegistration(activityId, userId);
        
        if (existingReg.isPresent()) {
            ActivityRegistration reg = existingReg.get();
            if ("REGISTERED".equals(reg.getStatus())) {
                throw new RuntimeException("您已报名该活动");
            }
            // 如果之前取消过，重新激活报名
            Activity activity = reg.getActivity();
            
            if (activity.getMaxParticipants() != null && 
                activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
                throw new RuntimeException("活动名额已满");
            }
            
            if (activity.getRegistrationDeadline() != null && 
                LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
                throw new RuntimeException("报名已截止");
            }
            
            reg.setStatus("REGISTERED");
            reg.setRegistrationTime(LocalDateTime.now());
            activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
            activityRepository.save(activity);
            
            return convertRegistrationToDTO(registrationRepository.save(reg));
        }
        
        // 新用户报名
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("活动未找到"));
        
        if (activity.getMaxParticipants() != null && 
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动名额已满");
        }
        
        if (activity.getRegistrationDeadline() != null && 
            LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            throw new RuntimeException("报名已截止");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        ActivityRegistration registration = ActivityRegistration.builder()
                .activity(activity)
                .user(user)
                .build();
        
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityRepository.save(activity);
        
        return convertRegistrationToDTO(registrationRepository.save(registration));
    }

    @Transactional
    public void cancelRegistration(Long activityId, Long userId) {
        ActivityRegistration registration = cleanupAndGetRegistration(activityId, userId)
                .filter(r -> "REGISTERED".equals(r.getStatus()))
                .orElseThrow(() -> new RuntimeException("未找到报名记录"));
        
        Activity activity = registration.getActivity();
        activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
        activityRepository.save(activity);
        
        registration.setStatus("CANCELLED");
        registrationRepository.save(registration);
    }

    @Transactional
    public ActivityRegistrationDTO checkIn(Long registrationId) {
        ActivityRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("报名记录未找到"));
        registration.setCheckInTime(LocalDateTime.now());
        registration.setStatus("CHECKED_IN");
        return convertRegistrationToDTO(registrationRepository.save(registration));
    }

    @Transactional
    public void toggleCollect(Long activityId, Long userId) {
        ActivityRegistration registration = cleanupAndGetRegistration(activityId, userId)
                .orElseGet(() -> {
                    Activity activity = activityRepository.findById(activityId)
                            .orElseThrow(() -> new RuntimeException("活动未找到"));
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("用户未找到"));
                    return ActivityRegistration.builder()
                            .activity(activity)
                            .user(user)
                            .status("COLLECTED")
                            .build();
                });
        registration.setIsCollected(!registration.getIsCollected());
        registrationRepository.save(registration);
    }

    // ========== 转换方法 ==========
    private ActivityDTO convertToDTO(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .coverImage(activity.getCoverImage())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .location(activity.getLocation())
                .maxParticipants(activity.getMaxParticipants())
                .currentParticipants(activity.getCurrentParticipants())
                .registrationDeadline(activity.getRegistrationDeadline())
                .status(activity.getStatus())
                .isHot(activity.getIsHot())
                .createdAt(activity.getCreatedAt())
                .build();
    }

    private ActivityRegistrationDTO convertRegistrationToDTO(ActivityRegistration reg) {
        return ActivityRegistrationDTO.builder()
                .id(reg.getId())
                .activityId(reg.getActivity().getId())
                .activityTitle(reg.getActivity().getTitle())
                .userId(reg.getUser().getId())
                .username(reg.getUser().getUsername())
                .registrationTime(reg.getRegistrationTime())
                .checkInTime(reg.getCheckInTime())
                .status(reg.getStatus())
                .isCollected(reg.getIsCollected())
                .build();
    }
}
