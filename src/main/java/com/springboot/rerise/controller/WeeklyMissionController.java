package com.springboot.rerise.controller;

import com.springboot.rerise.service.UserService;
import com.springboot.rerise.service.WeeklyMissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/missions")
public class WeeklyMissionController {

    @Autowired
    private WeeklyMissionService weeklyMissionService;
    @Autowired
    private UserService userService;

    /**
     * 사용자의 주간 미션을 조회합니다.
     * 미션 페이지 첫 접속 시 호출되며, 이번 주 미션이 없으면 자동 생성합니다.
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyMissionResponseDTO> getWeeklyMissions() {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            String email = (String) authentication.getPrincipal();
            Long userId = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId();

            log.info("사용자 {}의 주간 미션 요청", userId);

            // 주간 미션 생성/조회
            WeeklyMissionService.WeeklyMissionResult result = weeklyMissionService.generateWeeklyMissions(userId);

            // 응답 DTO 생성
            WeeklyMissionResponseDTO response = new WeeklyMissionResponseDTO();
            response.setUserId(userId);
            response.setSummaryMessage(result.getSummary());
            response.setRecommendedTheory(result.getRecommendedTheory());
            response.setThemes(result.getThemes());
            response.setMissions(result.getMissions().stream()
                .map(mission -> new MissionDTO(
                    mission.getMissionId(),
                    mission.getContent(),
                    mission.getTheme(),
                    mission.getMissionLevel(),
                    mission.getTheory().name(),
                    mission.getRewardExp()
                ))
                .collect(java.util.stream.Collectors.toList()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("주간 미션 조회 중 오류 발생", e);
            return ResponseEntity.status(500).build();
        }
    }

    // 응답 DTO 클래스들
    public static class WeeklyMissionResponseDTO {
        private Long userId;
        private String summaryMessage;
        private String recommendedTheory;
        private java.util.List<String> themes;
        private java.util.List<MissionDTO> missions;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getSummaryMessage() { return summaryMessage; }
        public void setSummaryMessage(String summaryMessage) { this.summaryMessage = summaryMessage; }
        
        public String getRecommendedTheory() { return recommendedTheory; }
        public void setRecommendedTheory(String recommendedTheory) { this.recommendedTheory = recommendedTheory; }
        
        public java.util.List<String> getThemes() { return themes; }
        public void setThemes(java.util.List<String> themes) { this.themes = themes; }
        
        public java.util.List<MissionDTO> getMissions() { return missions; }
        public void setMissions(java.util.List<MissionDTO> missions) { this.missions = missions; }
    }

    public static class MissionDTO {
        private Long missionId;
        private String content;
        private String theme;
        private int missionLevel;
        private String theory;
        private int rewardExp;

        public MissionDTO(Long missionId, String content, String theme, int missionLevel, String theory, int rewardExp) {
            this.missionId = missionId;
            this.content = content;
            this.theme = theme;
            this.missionLevel = missionLevel;
            this.theory = theory;
            this.rewardExp = rewardExp;
        }

        // Getters
        public Long getMissionId() { return missionId; }
        public String getContent() { return content; }
        public String getTheme() { return theme; }
        public int getMissionLevel() { return missionLevel; }
        public String getTheory() { return theory; }
        public int getRewardExp() { return rewardExp; }
    }
}