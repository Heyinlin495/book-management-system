package com.example._025021238heyinlin.controller;

import com.example._025021238heyinlin.dto.ApiResponse;
import com.example._025021238heyinlin.dto.StatisticsDTO;
import com.example._025021238heyinlin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<ApiResponse<StatisticsDTO>> getStatistics() {
        log.info("获取系统统计数据");
        return ResponseEntity.ok(ApiResponse.success(statisticsService.getStatistics()));
    }
}
