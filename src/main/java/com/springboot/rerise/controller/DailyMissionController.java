package com.springboot.rerise.controller;

import com.springboot.rerise.dto.DailyMissionResponseDTO;
import com.springboot.rerise.dto.MissionCompletionRequestDTO;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.service.DailyMissionService;
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
    
    @PostMapping("/daily")
    public ResponseEntity<List<DailyMissionResponseDTO>> generateDailyMissions(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        
        String userInput = request.getOrDefault("userInput", "오늘 하루 괜찮은 기분이에요");
        
        List<DailyMissionResponseDTO> missions = dailyMissionService
            .generateDailyMissions(user.getUser_id(), userInput);
        
        return ResponseEntity.ok(missions);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<DailyMissionResponseDTO>> getTodayMissions(
            @AuthenticationPrincipal User user) {
        
        List<DailyMissionResponseDTO> missions = dailyMissionService
            .getTodayMissions(user.getUser_id());
        
        return ResponseEntity.ok(missions);
    }
    
    @PostMapping("/complete")
    public ResponseEntity<DailyMissionResponseDTO> completeMission(
            @AuthenticationPrincipal User user,
            @RequestBody MissionCompletionRequestDTO request) {
        
        DailyMissionResponseDTO completedMission = dailyMissionService
            .completeMission(user.getUser_id(), request.getUserDailyMissionId());
        
        return ResponseEntity.ok(completedMission);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Daily Mission API is working!");
    }
}