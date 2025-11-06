package com.springboot.rerise.controller;

import com.springboot.rerise.dto.PlaceRecommendationResponseDTO;
import com.springboot.rerise.dto.ProgramRecommendationResponseDTO;
import com.springboot.rerise.entity.Program;
import com.springboot.rerise.service.PlaceRecommendationService;
import com.springboot.rerise.service.ProgramRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 장소 및 프로그램 추천 API 컨트롤러
 * 사용자의 감정, 키워드, 메모, 성향을 기반으로 용인시 기흥구 장소와 프로그램을 추천합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
public class PlaceRecommendationController {

    private final PlaceRecommendationService placeRecommendationService;
    private final ProgramRecommendationService programRecommendationService;

    /**
     * 용인시 기흥구 장소 추천 API
     *
     * @return 퍼플렉시티 AI가 추천한 장소 정보
     */
    @GetMapping("/places/seocho")
    public ResponseEntity<PlaceRecommendationResponseDTO> recommendPlacesInSeocho() {
        log.info("용인시 기흥구 장소 추천 요청이 들어왔습니다.");
        
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
     * 사용자 맞춤 프로그램 추천 API
     * 
     * @return 사용자 레벨과 성향에 맞는 프로그램 3개 추천
     */
    @GetMapping("/programs")
    public ResponseEntity<ProgramRecommendationResponseDTO> recommendPrograms() {
        log.info("프로그램 추천 요청이 들어왔습니다.");
        
        try {
            // 프로그램 추천 서비스 호출
            List<Program> recommendedPrograms = programRecommendationService.getRecommendedPrograms();
            
            // 추천 기준 설명 생성
            String reason = generateRecommendationReason(recommendedPrograms);
            
            log.info("프로그램 추천이 성공적으로 완료되었습니다. 추천된 프로그램 수: {}", recommendedPrograms.size());
            
            // 성공 응답 반환
            return ResponseEntity.ok(ProgramRecommendationResponseDTO.success(recommendedPrograms, reason));
            
        } catch (IllegalStateException e) {
            // 인증 관련 오류
            log.error("사용자 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(ProgramRecommendationResponseDTO.failure("로그인이 필요합니다."));
                    
        } catch (IllegalArgumentException e) {
            // 사용자 조회 오류
            log.error("사용자 조회 오류: {}", e.getMessage());
            return ResponseEntity.status(404)
                    .body(ProgramRecommendationResponseDTO.failure("사용자 정보를 찾을 수 없습니다."));
                    
        } catch (Exception e) {
            // 기타 서버 오류
            log.error("프로그램 추천 중 예상치 못한 오류가 발생했습니다: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ProgramRecommendationResponseDTO.failure("프로그램 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }

    /**
     * 추천 기준 설명을 생성합니다
     */
    private String generateRecommendationReason(List<Program> programs) {
        long youthProgramCount = programs.stream()
                .filter(Program::isYouthProgram)
                .count();
        
        long cultureProgramCount = programs.stream()
                .filter(Program::isCultureProgram)
                .count();
        
        if (youthProgramCount > cultureProgramCount) {
            return "회원님의 레벨이 높아 취업 및 커리어 관련 청년 프로그램을 우선적으로 추천드렸습니다.";
        } else if (cultureProgramCount > youthProgramCount) {
            return "회원님의 현재 상태를 고려하여 문화 및 여가 프로그램을 우선적으로 추천드렸습니다.";
        } else {
            return "회원님에게 다양한 분야의 프로그램을 균형있게 추천드렸습니다.";
        }
    }

    /**
     * 추천 서비스 상태 확인 API (헬스체크)
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("장소 및 프로그램 추천 서비스가 정상적으로 작동 중입니다.");
    }
}
