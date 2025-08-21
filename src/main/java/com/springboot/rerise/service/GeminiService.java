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
@RequiredArgsConstructor
public class GeminiService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
        
        return getDefaultThemes();
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
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = apiUrl + "?key=" + apiKey;
            HttpEntity<GeminiRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<GeminiResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, GeminiResponseDTO.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                GeminiResponseDTO responseBody = response.getBody();
                if (responseBody.getCandidates() != null && !responseBody.getCandidates().isEmpty()) {
                    GeminiResponseDTO.Candidate candidate = responseBody.getCandidates().get(0);
                    if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                        return candidate.getContent().getParts().get(0).getText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
        }
        return null;
    }
    
    private List<String> getDefaultThemes() {
        return Arrays.asList("마음보기", "몸돌보기", "마음나누기");
    }
    
    private boolean isValidTheory(String theory) {
        List<String> validTheories = Arrays.asList(
            "BEHAVIORAL_ACTIVATION", "COGNITIVE_RESTRUCTURING", 
            "MINDFULNESS", "SOCIAL_CONNECTION", "GRATITUDE_PRACTICE"
        );
        return validTheories.contains(theory);
    }
}