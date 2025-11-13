package com.springboot.rerise.service;

import com.springboot.rerise.entity.DailyRecord;
import com.springboot.rerise.entity.OnboardingAnswer;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserMissionProfile;
import com.springboot.rerise.repository.DailyRecordRepository;
import com.springboot.rerise.repository.OnboardingAnswerRepository;
import com.springboot.rerise.repository.UserMissionProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceRecommendationService {

    private final DailyRecordRepository dailyRecordRepository;
    private final OnboardingAnswerRepository onboardingAnswerRepository;
    private final UserMissionProfileRepository userMissionProfileRepository;
    private final UserService userService;
    private final PerplexityService perplexityService;

    @PostConstruct
    public void init() {
        log.info("PlaceRecommendationService loaded version=2025-11-13-test");
    }

    /**
     * 용인시 기흥구 장소 추천을 받는 메인 메서드
     */
    public String getPlaceRecommendation() {
        // 현재 로그인한 사용자 정보 가져오기
        User currentUser = getCurrentUser();
        
        // 사용자 데이터 종합 수집
        UserDataSummary userDataSummary = collectUserData(currentUser.getUserId());
        
        // 퍼플렉시티에게 보낼 시스템 프롬프트 생성
        String systemPrompt = createSystemPrompt();
        
        // 사용자 질문 프롬프트 생성
        String userPrompt = createUserPrompt(userDataSummary);
        
        // 퍼플렉시티 API 호출
        return perplexityService.askWithPersona(systemPrompt, userPrompt);
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("사용자가 로그인되어 있지 않습니다.");
        }
        
        String email = (String) authentication.getPrincipal();
        return userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * 사용자의 종합 데이터를 수집합니다
     */
    private UserDataSummary collectUserData(Long userId) {
        // 최근 7일간 일기 데이터
        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
        List<DailyRecord> recentRecords = dailyRecordRepository
                .findByUserUserIdAndRecordedAtBetween(userId, oneWeekAgo, LocalDate.now());

        // 감정 레벨 요약
        String emotionSummary = summarizeEmotions(recentRecords);
        
        // 키워드 요약
        String keywordSummary = summarizeKeywords(recentRecords);
        
        // 메모 요약
        String memoSummary = summarizeMemos(recentRecords);
        
        // 온보딩 성향 요약
        String personalitySummary = summarizePersonality(userId);
        
        return new UserDataSummary(emotionSummary, keywordSummary, memoSummary, personalitySummary);
    }

    /**
     * 감정 레벨을 요약합니다
     */
    private String summarizeEmotions(List<DailyRecord> records) {
        if (records.isEmpty()) {
            return "최근 7일간 감정 기록이 없습니다.";
        }
        
        double avgEmotion = records.stream()
                .mapToInt(DailyRecord::getEmotion_level)
                .average()
                .orElse(3.0);
        
        // 감정 패턴 분석
        String emotionPattern;
        if (avgEmotion >= 4.0) {
            emotionPattern = "매우 긍정적";
        } else if (avgEmotion >= 3.5) {
            emotionPattern = "긍정적";
        } else if (avgEmotion >= 2.5) {
            emotionPattern = "보통";
        } else if (avgEmotion >= 2.0) {
            emotionPattern = "다소 부정적";
        } else {
            emotionPattern = "부정적";
        }
        
        return String.format("평균 감정: %.1f/5 (%s)", avgEmotion, emotionPattern);
    }

    /**
     * 키워드를 요약합니다
     */
    private String summarizeKeywords(List<DailyRecord> records) {
        List<String> allKeywords = records.stream()
                .filter(r -> r.getKeywords() != null)
                .flatMap(r -> r.getKeywords().stream())
                .collect(Collectors.toList());
        
        if (allKeywords.isEmpty()) {
            return "최근 7일간 키워드 기록이 없습니다.";
        }
        
        // 중복 제거하고 상위 5개 키워드 반환
        String topKeywords = allKeywords.stream()
                .distinct()
                .limit(5)
                .collect(Collectors.joining(", "));
                
        return String.format("주요 키워드: %s", topKeywords);
    }

    /**
     * 메모를 요약합니다
     */
    private String summarizeMemos(List<DailyRecord> records) {
        List<String> memos = records.stream()
                .filter(r -> r.getMemo() != null && !r.getMemo().trim().isEmpty())
                .map(r -> r.getMemo().substring(0, Math.min(30, r.getMemo().length())))
                .collect(Collectors.toList());
        
        if (memos.isEmpty()) {
            return "최근 7일간 메모 기록이 없습니다.";
        }
        
        return String.format("최근 메모 내용: %s", String.join(" / ", memos));
    }

    /**
     * 온보딩 답변을 기반으로 사용자 성향을 요약합니다
     */
    private String summarizePersonality(Long userId) {
        List<OnboardingAnswer> answers = onboardingAnswerRepository.findByUserId(userId);
        
        if (answers.isEmpty()) {
            return "온보딩 정보가 없습니다.";
        }
        
        // 미션 프로필 정보 가져오기
        Optional<UserMissionProfile> profileOptional = userMissionProfileRepository.findByUserUserId(userId);
        
        if (profileOptional.isEmpty()) {
            return "사용자 성향 분석 결과가 없습니다.";
        }
        
        UserMissionProfile profile = profileOptional.get();
        
        // 가중치를 기반으로 선호 테마 분석
        StringBuilder themes = new StringBuilder();
        
        if (profile.getMindfulnessWeight() > 1.2) {
            themes.append("마음챙김 ");
        }
        if (profile.getBehavioralActivationWeight() > 1.2) {
            themes.append("활동적인 참여 ");
        }
        if (profile.getCognitiveRestructuringWeight() > 1.2) {
            themes.append("사고 정리 ");
        }
        if (profile.getSocialConnectionWeight() > 1.2) {
            themes.append("사회적 연결 ");
        }
        if (profile.getGratitudePracticeWeight() > 1.2) {
            themes.append("감사 실천 ");
        }
        
        String personalityTraits = themes.toString().trim();
        if (personalityTraits.isEmpty()) {
            personalityTraits = "균형 잡힌 성향";
        }
        
        String energyType = profile.getEnergyRecoveryFocus() ? "에너지 회복 중심" : "성장 지향적";
        
        return String.format("성향: %s, %s", personalityTraits, energyType);
    }

    /**
     * 퍼플렉시티에게 보낼 시스템 프롬프트를 생성합니다
     */
    private String createSystemPrompt() {
        return """
                당신은 친근하고 따뜻한 장소 추천 전문가입니다.
                사용자의 최근 감정 상태, 키워드, 메모 내용, 그리고 성향을 종합적으로 분석하여
                용인시 기흥구에서 갈 만한 장소를 1곳 추천해주세요.

                추천 조건:
                1. 용인시 기흥구 내의 실제 존재하는 장소만 추천
                2. 사용자의 현재 감정 상태에 맞는 장소
                3. 사용자의 키워드와 메모 내용을 반영한 장소
                4. 사용자의 성향(온보딩 답변)을 고려한 장소

                응답 형식:
                🌟 [장소명]
                📍 위치: [구체적 주소](https://map.naver.com/v5/search/[구체적 주소 URL 인코딩])
                💡 추천 이유: [사용자의 감정/키워드/메모/성향을 구체적으로 언급하며 친근하게 설명]
                ⏰ 방문 팁: [언제 가면 좋은지, 주의사항 등]

                말투는 친구처럼 따뜻하고 친근하게 해주세요.
                """;
    }

    /**
     * 사용자 데이터를 기반으로 질문 프롬프트를 생성합니다
     */
    private String createUserPrompt(UserDataSummary userDataSummary) {
        return String.format("""
                안녕하세요! 용인시 기흥구에서 갈 만한 곳을 추천받고 싶어요.

                📊 **최근 7일간 나의 상태:**
                - 감정 상태: %s
                - %s
                - %s
                - 나의 성향: %s

                이런 나에게 용인시 기흥구에서 갈 만한 곳을 추천해주세요!
                """,
                userDataSummary.getEmotionSummary(),
                userDataSummary.getKeywordSummary(),
                userDataSummary.getMemoSummary(),
                userDataSummary.getPersonalitySummary());
    }

    /**
     * 사용자 데이터 요약을 담는 내부 클래스
     */
    private static class UserDataSummary {
        private final String emotionSummary;
        private final String keywordSummary;
        private final String memoSummary;
        private final String personalitySummary;

        public UserDataSummary(String emotionSummary, String keywordSummary, 
                              String memoSummary, String personalitySummary) {
            this.emotionSummary = emotionSummary;
            this.keywordSummary = keywordSummary;
            this.memoSummary = memoSummary;
            this.personalitySummary = personalitySummary;
        }

        public String getEmotionSummary() { return emotionSummary; }
        public String getKeywordSummary() { return keywordSummary; }
        public String getMemoSummary() { return memoSummary; }
        public String getPersonalitySummary() { return personalitySummary; }
    }
}
