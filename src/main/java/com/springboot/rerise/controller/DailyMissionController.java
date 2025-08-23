package com.springboot.rerise.controller;

import com.springboot.rerise.dto.DailyMissionResponseDTO;
import com.springboot.rerise.dto.MissionCompletionRequestDTO;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.service.DailyMissionService;
import com.springboot.rerise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class DailyMissionController {
    
    private final DailyMissionService dailyMissionService;
    private final UserService userService;
    
    @PostMapping("/daily")
    public ResponseEntity<List<DailyMissionResponseDTO>> generateDailyMissions(
            @AuthenticationPrincipal String email,
            @RequestBody Map<String, String> request) {
        
        String userInput = request.getOrDefault("userInput", "오늘 하루 괜찮은 기분이에요");
        
        // 이메일로 사용자 ID 조회
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        List<DailyMissionResponseDTO> missions = dailyMissionService
            .generateDailyMissions(user.getUserId(), userInput);
        
        return ResponseEntity.ok(missions);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<DailyMissionResponseDTO>> getTodayMissions(
            @AuthenticationPrincipal String email) {
        
        // 이메일로 사용자 ID 조회
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        List<DailyMissionResponseDTO> missions = dailyMissionService
            .getTodayMissions(user.getUserId());
        
        return ResponseEntity.ok(missions);
    }
    
    @PostMapping("/complete")
    public ResponseEntity<DailyMissionResponseDTO> completeMission(
            @AuthenticationPrincipal String email,
            @RequestBody MissionCompletionRequestDTO request) {
        
        // 이메일로 사용자 ID 조회
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        DailyMissionResponseDTO completedMission = dailyMissionService
            .completeMission(user.getUserId(), request.getUserDailyMissionId());
        
        return ResponseEntity.ok(completedMission);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Daily Mission API is working!");
    }
}