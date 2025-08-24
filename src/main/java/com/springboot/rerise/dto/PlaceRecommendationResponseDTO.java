package com.springboot.rerise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 장소 추천 API 응답을 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRecommendationResponseDTO {
    
    /**
     * 퍼플렉시티 AI로부터 받은 장소 추천 결과
     */
    private String recommendation;
    
    /**
     * 요청 처리 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지 (에러 발생 시 에러 메시지)
     */
    private String message;
    
    /**
     * 성공적인 추천을 위한 생성자
     */
    public static PlaceRecommendationResponseDTO success(String recommendation) {
        return new PlaceRecommendationResponseDTO(recommendation, true, "장소 추천이 성공적으로 완료되었습니다.");
    }
    
    /**
     * 실패한 추천을 위한 생성자
     */
    public static PlaceRecommendationResponseDTO failure(String errorMessage) {
        return new PlaceRecommendationResponseDTO(null, false, errorMessage);
    }
}
