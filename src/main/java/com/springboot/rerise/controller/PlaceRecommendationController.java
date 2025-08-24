package com.springboot.rerise.controller;

import com.springboot.rerise.dto.PlaceRecommendationResponseDTO;
import com.springboot.rerise.service.PlaceRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 장소 추천 API 컨트롤러
 * 사용자의 감정, 키워드, 메모, 성향을 기반으로 서울 서초구 장소를 추천합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/place")
@RequiredArgsConstructor
public class PlaceRecommendationController {

    private final PlaceRecommendationService placeRecommendationService;

    /**
     * 서울 서초구 장소 추천 API
     * 
     * @return 퍼플렉시티 AI가 추천한 장소 정보
     */
    @GetMapping("/recommend/seocho")
    public ResponseEntity<PlaceRecommendationResponseDTO> recommendPlacesInSeocho() {
        log.info("서울 서초구 장소 추천 요청이 들어왔습니다.");
        
        try {
            // 장소 추천 서비스 호출
            String recommendation = placeRecommendationService.getPlaceRecommendation();
            
            log.info("장소 추천이 성공적으로 완료되었습니다.");
            
            // 성공 응답 반환
            return ResponseEntity.ok(PlaceRecommendationResponseDTO.success(recommendation));
            
        } catch (IllegalStateException e) {
            // 인증 관련 오류
            log.error("사용자 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(PlaceRecommendationResponseDTO.failure("로그인이 필요합니다."));
                    
        } catch (IllegalArgumentException e) {
            // 사용자 조회 오류
            log.error("사용자 조회 오류: {}", e.getMessage());
            return ResponseEntity.status(404)
                    .body(PlaceRecommendationResponseDTO.failure("사용자 정보를 찾을 수 없습니다."));
                    
        } catch (Exception e) {
            // 기타 서버 오류
            log.error("장소 추천 중 예상치 못한 오류가 발생했습니다: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(PlaceRecommendationResponseDTO.failure("장소 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }
    
    /**
     * 장소 추천 기능 상태 확인 API (헬스체크)
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("장소 추천 서비스가 정상적으로 작동 중입니다.");
    }
}
