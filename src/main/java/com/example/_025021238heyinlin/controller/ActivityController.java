package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.ActivityDTO;
import com.example._025021238heyinlin.dto.ActivityRegistrationDTO;
import com.example._025021238heyinlin.jwt.UserDetailsImpl;
import com.example._025021238heyinlin.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ActivityController {

    private final ActivityService activityService;

    // ========== 活动接口 ==========
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getAllActivities() {
        return ResponseEntity.ok(ApiResponse.success(activityService.getAllActivities()));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getUpcomingActivities() {
        return ResponseEntity.ok(ApiResponse.success(activityService.getUpcomingActivities()));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getHotActivities() {
        return ResponseEntity.ok(ApiResponse.success(activityService.getHotActivities()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDTO>> getActivityById(@PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            ActivityDTO activity = userId != null ?
                    activityService.getActivityByIdForUser(id, userId) :
                    activityService.getActivityById(id).orElseThrow(() -> new RuntimeException("活动未找到"));
            return ResponseEntity.ok(ApiResponse.success(activity));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityDTO>> createActivity(@RequestBody ActivityDTO dto) {
        try {
            ActivityDTO created = activityService.createActivity(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("活动创建成功", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDTO>> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO dto) {
        try {
            ActivityDTO updated = activityService.updateActivity(id, dto);
            return ResponseEntity.ok(ApiResponse.success("活动更新成功", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success("活动删除成功", null));
    }

    // ========== 活动报名接口 ==========
    @GetMapping("/{activityId}/registrations")
    public ResponseEntity<ApiResponse<List<ActivityRegistrationDTO>>> getRegistrationsByActivity(@PathVariable Long activityId) {
        return ResponseEntity.ok(ApiResponse.success(activityService.getRegistrationsByActivity(activityId)));
    }

    @GetMapping("/registrations/me")
    public ResponseEntity<ApiResponse<List<ActivityRegistrationDTO>>> getMyRegistrations(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(activityService.getMyRegistrations(userId)));
    }

    @GetMapping("/registrations/collected/me")
    public ResponseEntity<ApiResponse<List<ActivityRegistrationDTO>>> getMyCollectedActivities(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.success(activityService.getMyCollectedActivities(userId)));
    }

    @PostMapping("/{activityId}/register")
    public ResponseEntity<ApiResponse<ActivityRegistrationDTO>> registerActivity(
            @PathVariable Long activityId, Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        try {
            ActivityRegistrationDTO registration = activityService.registerActivity(activityId, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("报名成功", registration));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/{activityId}/register")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @PathVariable Long activityId, Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        try {
            activityService.cancelRegistration(activityId, userId);
            return ResponseEntity.ok(ApiResponse.success("取消报名成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/registrations/{registrationId}/checkin")
    public ResponseEntity<ApiResponse<ActivityRegistrationDTO>> checkIn(@PathVariable Long registrationId) {
        try {
            ActivityRegistrationDTO registration = activityService.checkIn(registrationId);
            return ResponseEntity.ok(ApiResponse.success("签到成功", registration));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/{activityId}/collect")
    public ResponseEntity<ApiResponse<Void>> toggleCollect(
            @PathVariable Long activityId, Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        try {
            activityService.toggleCollect(activityId, userId);
            return ResponseEntity.ok(ApiResponse.success("操作成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return null;
    }
}
