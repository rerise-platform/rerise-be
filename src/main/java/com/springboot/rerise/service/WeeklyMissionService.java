package com.springboot.rerise.service;

import com.springboot.rerise.entity.DailyRecord;
import com.springboot.rerise.entity.Missions;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.entity.UserMissionProfile;
import com.springboot.rerise.repository.DailyRecordRepository;
import com.springboot.rerise.repository.MissionsRepository;
import com.springboot.rerise.repository.UserCharacterRepository;
import com.springboot.rerise.repository.UserMissionProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeeklyMissionService {

    @Autowired
    private DailyRecordRepository dailyRecordRepository;
    @Autowired
    private UserMissionProfileRepository userMissionProfileRepository;
    @Autowired
    private UserCharacterRepository userCharacterRepository;
    @Autowired
    private MissionsRepository missionsRepository;
    @Autowired
    private GeminiService geminiService;

    /**
     * 사용자의 주간 미션을 생성합니다.
     */
    public WeeklyMissionResult generateWeeklyMissions(Long userId) {
        try {
            log.info("사용자 {}의 주간 미션 생성 시작", userId);

            // 1. 사용자 데이터 수집
            UserDataSummary userData = collectUserData(userId);
            
            // 2. Gemini AI 분석 요청
            GeminiService.WeeklyMissionAnalysis analysis = geminiService.analyzeWeeklyMissionNeeds(
                userData.getEmotionSummary(),
                userData.getDiarySummary(), 
                userData.getMissionProfileSummary()
            );
            
            // 3. 분석 결과를 바탕으로 DB에서 미션 3개 조회
            List<Missions> selectedMissions = selectMissionsFromDB(
                userId, 
                analysis.getTheory(),
                analysis.getThemes(), 
                analysis.getRecommendedLevel()
            );
            
            // 4. 결과 반환
            return new WeeklyMissionResult(
                analysis.getSummary(),
                selectedMissions,
                analysis.getTheory(),
                analysis.getThemes()
            );

        } catch (Exception e) {
            log.error("주간 미션 생성 중 오류 발생 - userId: {}", userId, e);
            return createFallbackMissions(userId);
        }
    }

    /**
     * 사용자의 종합 데이터를 수집합니다.
     */
    private UserDataSummary collectUserData(Long userId) {
        // 최근 7일간 일기 데이터  
        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
        List<DailyRecord> recentRecords = dailyRecordRepository
            .findByUserUserIdAndRecordedAtBetween(userId, oneWeekAgo, LocalDate.now());

        // 감정 레벨 요약
        String emotionSummary = summarizeEmotions(recentRecords);
        
        // 일기 내용 요약  
        String diarySummary = summarizeDiaryContent(recentRecords);
        
        // 온보딩 기반 미션 선호도
        String missionProfileSummary = summarizeMissionProfile(userId);
        
        return new UserDataSummary(emotionSummary, diarySummary, missionProfileSummary);
    }

    private String summarizeEmotions(List<DailyRecord> records) {
        if (records.isEmpty()) {
            return "최근 7일간 감정 기록이 없습니다.";
        }
        
        double avgEmotion = records.stream()
            .mapToInt(DailyRecord::getEmotion_level)
            .average()
            .orElse(3.0);
            
        List<String> allKeywords = records.stream()
            .filter(r -> r.getKeywords() != null)
            .flatMap(r -> r.getKeywords().stream())
            .collect(Collectors.toList());
            
        return String.format("평균 감정: %.1f/5, 주요 키워드: %s", 
            avgEmotion, 
            allKeywords.stream().limit(5).collect(Collectors.joining(", ")));
    }
    
    private String summarizeDiaryContent(List<DailyRecord> records) {
        if (records.isEmpty()) {
            return "최근 일기 기록이 없습니다.";
        }
        
        String combinedMemos = records.stream()
            .filter(r -> r.getMemo() != null && !r.getMemo().trim().isEmpty())
            .map(DailyRecord::getMemo)
            .limit(3) // 최근 3개만
            .collect(Collectors.joining(" / "));
            
        return combinedMemos.isEmpty() ? "일기 내용이 없습니다." : combinedMemos;
    }
    
    private String summarizeMissionProfile(Long userId) {
        return userMissionProfileRepository.findByUserUserId(userId)
            .map(profile -> String.format(
                "선호 이론 - MINDFULNESS: %.1f, BEHAVIORAL_ACTIVATION: %.1f, SOCIAL_CONNECTION: %.1f",
                profile.getMindfulnessWeight(),
                profile.getBehavioralActivationWeight(), 
                profile.getSocialConnectionWeight()))
            .orElse("온보딩 프로필이 없습니다.");
    }

    /**
     * AI 분석 결과를 바탕으로 DB에서 적절한 미션들을 선별합니다.
     */
    private List<Missions> selectMissionsFromDB(Long userId, String theory, List<String> themes, int recommendedLevel) {
        // 사용자 레벨 확인
        int userLevel = getUserLevel(userId);
        
        // 레벨 범위 설정 (권장 레벨 ±1)
        int minLevel = Math.max(1, Math.min(recommendedLevel - 1, userLevel));
        int maxLevel = Math.min(3, Math.max(recommendedLevel + 1, userLevel + 1));
        
        log.info("미션 선별 조건 - 이론: {}, 테마: {}, 레벨 범위: {}-{}", theory, themes, minLevel, maxLevel);
        
        // String을 ENUM으로 변환
        Missions.MissionTheory theoryEnum;
        try {
            theoryEnum = Missions.MissionTheory.valueOf(theory);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 이론 값: {}, 기본값 MINDFULNESS 사용", theory);
            theoryEnum = Missions.MissionTheory.MINDFULNESS;
        }
        
        // 조건에 맞는 미션들 조회 (각 테마별로 1개씩 최대 3개)
        List<Missions> selectedMissions = missionsRepository
            .findByTheoryAndThemeInAndMissionLevelBetween(theoryEnum, themes, minLevel, maxLevel)
            .stream()
            .limit(3)
            .collect(Collectors.toList());
            
        // 3개가 안 되면 레벨 조건 완화해서 추가 조회
        if (selectedMissions.size() < 3) {
            List<Missions> additionalMissions = missionsRepository
                .findByThemeInAndMissionLevelBetween(themes, minLevel, maxLevel)
                .stream()
                .filter(m -> !selectedMissions.contains(m))
                .limit(3 - selectedMissions.size())
                .collect(Collectors.toList());
            selectedMissions.addAll(additionalMissions);
        }
        
        return selectedMissions;
    }
    
    private int getUserLevel(Long userId) {
        return userCharacterRepository.findByUserUserId(userId)
            .map(UserCharacter::getLevel)
            .orElse(1);
    }

    /**
     * AI 분석 실패 시 기본 미션을 반환합니다.
     */
    private WeeklyMissionResult createFallbackMissions(Long userId) {
        int userLevel = getUserLevel(userId);
        List<Missions> fallbackMissions = missionsRepository
            .findByMissionLevelBetween(userLevel, userLevel + 1)
            .stream()
            .limit(3)
            .collect(Collectors.toList());
            
        return new WeeklyMissionResult(
            "이번 주도 차근차근 나아가보세요.\n작은 실천이 큰 변화를 만듭니다.",
            fallbackMissions,
            "MINDFULNESS",
            List.of("마음보기", "몸돌보기", "마음나누기")
        );
    }

    // 데이터 클래스들
    public static class UserDataSummary {
        private final String emotionSummary;
        private final String diarySummary;
        private final String missionProfileSummary;
        
        public UserDataSummary(String emotionSummary, String diarySummary, String missionProfileSummary) {
            this.emotionSummary = emotionSummary;
            this.diarySummary = diarySummary;
            this.missionProfileSummary = missionProfileSummary;
        }
        
        public String getEmotionSummary() { return emotionSummary; }
        public String getDiarySummary() { return diarySummary; }
        public String getMissionProfileSummary() { return missionProfileSummary; }
    }
    
    public static class WeeklyMissionResult {
        private final String summary;
        private final List<Missions> missions;
        private final String recommendedTheory;
        private final List<String> themes;
        
        public WeeklyMissionResult(String summary, List<Missions> missions, String recommendedTheory, List<String> themes) {
            this.summary = summary;
            this.missions = missions;
            this.recommendedTheory = recommendedTheory;
            this.themes = themes;
        }
        
        public String getSummary() { return summary; }
        public List<Missions> getMissions() { return missions; }
        public String getRecommendedTheory() { return recommendedTheory; }
        public List<String> getThemes() { return themes; }
    }
}