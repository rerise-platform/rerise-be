package com.springboot.rerise.dto;

import com.springboot.rerise.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 프로그램 추천 API 응답을 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRecommendationResponseDTO {
    
    /**
     * 추천된 프로그램 목록
     */
    private List<ProgramDTO> programs;
    
    /**
     * 추천 기준 설명
     */
    private String recommendationReason;
    
    /**
     * 요청 처리 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 개별 프로그램 정보를 담는 내부 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgramDTO {
        
        /**
         * 프로그램명
         */
        private String programName;
        
        /**
         * 카테고리 (청년/문화)
         */
        private String category;
        
        /**
         * 대상
         */
        private String target;
        
        /**
         * 모집기간
         */
        private String recruitmentPeriod;
        
        /**
         * 위치
         */
        private String location;
        
        /**
         * URL
         */
        private String url;
        
        /**
         * Program 엔티티를 DTO로 변환하는 정적 메서드
         */
        public static ProgramDTO fromEntity(Program program) {
            return new ProgramDTO(
                program.getProgramName(),
                program.getCategory().isEmpty() ? "청년" : program.getCategory(),
                program.getTarget(),
                program.getRecruitmentPeriod(),
                program.getLocation(),
                program.getUrl()
            );
        }
    }
    
    /**
     * 성공적인 추천을 위한 생성자
     */
    public static ProgramRecommendationResponseDTO success(List<Program> programs, String reason) {
        List<ProgramDTO> programDTOs = programs.stream()
                .map(ProgramDTO::fromEntity)
                .toList();
        
        return new ProgramRecommendationResponseDTO(
            programDTOs,
            reason,
            true,
            "프로그램 추천이 성공적으로 완료되었습니다."
        );
    }
    
    /**
     * 실패한 추천을 위한 생성자
     */
    public static ProgramRecommendationResponseDTO failure(String errorMessage) {
        return new ProgramRecommendationResponseDTO(
            null,
            null,
            false,
            errorMessage
        );
    }
}
