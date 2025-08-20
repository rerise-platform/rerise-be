package com.springboot.rerise.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//테스트 결과를 프론트엔드로 전송할 때 사용
@Getter @Setter
public class OnboardingResultResponseDTO {
    private Long characterId;
    private Long userId;
    private String characterType;
    private String description;
    private List<String> keywords;

    private int energyLevel;
    private int adaptability;
    private int Resilience;
}
