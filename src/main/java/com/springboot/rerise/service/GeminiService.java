package com.springboot.rerise.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.rerise.dto.GeminiRequestDTO;
import com.springboot.rerise.dto.GeminiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class GeminiService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public GeminiService() {
        this.restTemplate = new RestTemplate();
        // 타임아웃 설정 (30초)
        restTemplate.setRequestFactory(createTimeoutRequestFactory());
    }
    
    private org.springframework.http.client.ClientHttpRequestFactory createTimeoutRequestFactory() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 연결 타임아웃 10초
        factory.setReadTimeout(30000);    // 읽기 타임아웃 30초
        return factory;
    }
    
    public List<String> analyzeUserState(String userInput, String recentDiaryContext) {
        try {
            String prompt = String.format(
                "사용자의 현재 상태와 최근 일기 내용을 종합적으로 분석하여 적합한 정신건강 활동 테마를 3개 추천해주세요. " +
                "다음 테마 중에서만 선택하세요: 마음보기, 몸돌보기, 마음나누기, 공간만들기, 사람연결. " +
                "현재 입력: %s " +
                "최근 일기 맥락: %s " +
                "응답은 쉼표로 구분된 테마 이름만 반환하세요. 예: 마음보기,몸돌보기,마음나누기", 
                userInput, recentDiaryContext != null ? recentDiaryContext : "일기 기록 없음"
            );
            
            GeminiRequestDTO request = createGeminiRequest(prompt);
            String response = callGeminiAPI(request);
            
            if (response != null && !response.trim().isEmpty()) {
                return Arrays.asList(response.split(","));
            }
            
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생", e);
        }


        return List.of();
    }

    public List<String> analyzeUserState(String userInput) {
        return analyzeUserState(userInput, null);
    }
    
    public String getRecommendedTheory(List<String> themes) {
        try {
            String prompt = String.format(
                "다음 테마들(%s)에 가장 적합한 심리학적 이론을 하나 선택해주세요. " +
                "선택지: BEHAVIORAL_ACTIVATION, COGNITIVE_RESTRUCTURING, MINDFULNESS, SOCIAL_CONNECTION, GRATITUDE_PRACTICE " +
                "이론 이름만 정확히 반환하세요.",
                String.join(", ", themes)
            );
            
            GeminiRequestDTO request = createGeminiRequest(prompt);
            String response = callGeminiAPI(request);
            
            if (response != null && isValidTheory(response.trim())) {
                return response.trim();
            }
            
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생", e);
        }
        
        return "MINDFULNESS";
    }
    
    private GeminiRequestDTO createGeminiRequest(String prompt) {
        GeminiRequestDTO.Part part = new GeminiRequestDTO.Part(prompt);
        GeminiRequestDTO.Content content = new GeminiRequestDTO.Content(Collections.singletonList(part));
        return new GeminiRequestDTO(Collections.singletonList(content));
    }
    
    private String callGeminiAPI(GeminiRequestDTO request) {
        // API 키 유효성 검사
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("GEMINI_API_KEY 환경변수가 설정되지 않았습니다.");
            return null;
        }
        
        log.info("Gemini API 호출 시작: URL = {}", apiUrl);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = apiUrl + "?key=" + apiKey;
            HttpEntity<GeminiRequestDTO> entity = new HttpEntity<>(request, headers);
            
            log.info("요청 데이터: {}", request);
            
            ResponseEntity<GeminiResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, GeminiResponseDTO.class
            );
            
            log.info("응답 상태: {}", response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                GeminiResponseDTO responseBody = response.getBody();
                if (responseBody.getCandidates() != null && !responseBody.getCandidates().isEmpty()) {
                    GeminiResponseDTO.Candidate candidate = responseBody.getCandidates().get(0);
                    if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                        String result = candidate.getContent().getParts().get(0).getText();
                        log.info("Gemini API 응답: {}", result);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Gemini API 호출 실패 - URL: {}, 에러: {}", apiUrl, e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 사용자의 일주일치 데이터를 분석하여 적절한 심리학 이론, 테마, 요약문장을 생성
     */
    public WeeklyMissionAnalysis analyzeWeeklyMissionNeeds(
            String emotionSummary, 
            String diarySummary, 
            String missionProfileSummary) {
        
        try {
            String prompt = String.format(
                "사용자의 최근 7일간 데이터를 분석하여 이번 주 미션 방향을 결정해주세요.\n\n" +
                "=== 감정 데이터 ===\n%s\n\n" +
                "=== 일기 요약 ===\n%s\n\n" +
                "=== 온보딩 기반 선호도 ===\n%s\n\n" +
                "다음 형식으로 정확히 응답해주세요:\n" +
                "THEORY: [BEHAVIORAL_ACTIVATION, COGNITIVE_RESTRUCTURING, MINDFULNESS, SOCIAL_CONNECTION, GRATITUDE_PRACTICE 중 1개]\n" +
                "THEMES: [마음보기, 몸돌보기, 마음나누기, 공간만들기, 사람연결 중 3개, 쉼표로 구분]\n" +
                "SUMMARY: [사용자 상황을 반영한 격려 메시지 2줄, 각 줄은 50자 이내]\n" +
                "LEVEL: [1, 2, 3 중 권장 난이도]",
                emotionSummary, diarySummary, missionProfileSummary
            );
            
            GeminiRequestDTO request = createGeminiRequest(prompt);
            String response = callGeminiAPI(request);
            
            if (response != null && !response.trim().isEmpty()) {
                return parseWeeklyMissionResponse(response);
            }
            
        } catch (Exception e) {
            log.error("주간 미션 분석 중 오류 발생", e);
        }
        
        // 기본값 반환
        return new WeeklyMissionAnalysis(
            "MINDFULNESS", 
            Arrays.asList("마음보기", "몸돌보기", "마음나누기"),
            "이번 주도 차근차근 나아가보세요.\n작은 변화가 큰 성장을 만듭니다.",
            2
        );
    }
    
    private WeeklyMissionAnalysis parseWeeklyMissionResponse(String response) {
        try {
            String[] lines = response.split("\n");
            String theory = "MINDFULNESS";
            List<String> themes = Arrays.asList("마음보기", "몸돌보기", "마음나누기");
            String summary = "이번 주도 차근차근 나아가보세요.\n작은 변화가 큰 성장을 만듭니다.";
            int level = 2;
            
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("THEORY:")) {
                    String theoryCandidate = line.substring(7).trim();
                    if (isValidTheory(theoryCandidate)) {
                        theory = theoryCandidate;
                    }
                } else if (line.startsWith("THEMES:")) {
                    String themesStr = line.substring(7).trim();
                    String[] themeArray = themesStr.split(",");
                    themes = Arrays.stream(themeArray)
                            .map(String::trim)
                            .limit(3)
                            .collect(java.util.stream.Collectors.toList());
                } else if (line.startsWith("SUMMARY:")) {
                    summary = line.substring(8).trim();
                } else if (line.startsWith("LEVEL:")) {
                    try {
                        level = Integer.parseInt(line.substring(6).trim());
                        if (level < 1 || level > 3) level = 2;
                    } catch (NumberFormatException e) {
                        level = 2;
                    }
                }
            }
            
            return new WeeklyMissionAnalysis(theory, themes, summary, level);
            
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 오류", e);
            return new WeeklyMissionAnalysis(
                "MINDFULNESS", 
                Arrays.asList("마음보기", "몸돌보기", "마음나누기"),
                "이번 주도 차근차근 나아가보세요.\n작은 변화가 큰 성장을 만듭니다.",
                2
            );
        }
    }
    
    private boolean isValidTheory(String theory) {
        List<String> validTheories = Arrays.asList(
            "BEHAVIORAL_ACTIVATION", "COGNITIVE_RESTRUCTURING", 
            "MINDFULNESS", "SOCIAL_CONNECTION", "GRATITUDE_PRACTICE"
        );
        return validTheories.contains(theory);
    }
    
    // 응답 데이터 클래스
    public static class WeeklyMissionAnalysis {
        private final String theory;
        private final List<String> themes;
        private final String summary;
        private final int recommendedLevel;
        
        public WeeklyMissionAnalysis(String theory, List<String> themes, String summary, int recommendedLevel) {
            this.theory = theory;
            this.themes = themes;
            this.summary = summary;
            this.recommendedLevel = recommendedLevel;
        }
        
        public String getTheory() { return theory; }
        public List<String> getThemes() { return themes; }
        public String getSummary() { return summary; }
        public int getRecommendedLevel() { return recommendedLevel; }
    }
}