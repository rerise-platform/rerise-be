package com.springboot.rerise.service;

import com.springboot.rerise.dto.Message;
import com.springboot.rerise.dto.PerplexityRequest;
import com.springboot.rerise.dto.PerplexityResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays; // Arrays import 추가
import java.util.List;   // List import 추가

@Service
public class PerplexityService {

    private final RestTemplate restTemplate;

    @Value("${perplexity.api.key}")
    private String apiKey;

    @Value("${perplexity.api.url}")
    private String apiUrl;

    public PerplexityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Perplexity API에 페르소나를 설정하여 질문하는 메서드
     * @param systemPrompt AI에게 부여할 페르소나 (역할, 행동 지침)
     * @param userPrompt 사용자 실제 질문
     * @return API의 답변 문자열
     */
    public String askWithPersona(String systemPrompt, String userPrompt) {
        // 1. HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // 2. 요청 본문(Body)에 포함될 메시지 목록 생성
        // ▼▼▼ 페르소나(system)와 실제 질문(user) 메시지를 모두 생성합니다. ▼▼▼
        List<Message> messages = Arrays.asList(
                new Message("system", systemPrompt),
                new Message("user", userPrompt)
        );

        PerplexityRequest requestBody = new PerplexityRequest("sonar", messages);

        // 3. 요청 엔티티 생성
        HttpEntity<PerplexityRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        // 4. API에 POST 요청 보내기
        try {
            ResponseEntity<PerplexityResponse> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    PerplexityResponse.class
            );

            // 5. 응답에서 답변 추출하기
            if (responseEntity.getBody() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                return responseEntity.getBody().getChoices().get(0).getMessage().getContent();
            }

        } catch (Exception e) {
            System.err.println("API 호출 중 오류 발생: " + e.getMessage());
            return "죄송합니다, 답변을 가져오는 데 문제가 발생했습니다.";
        }

        return "죄송합니다, 유효한 답변을 받지 못했습니다.";
    }
}