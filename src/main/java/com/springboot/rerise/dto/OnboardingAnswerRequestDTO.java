package com.springboot.rerise.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//프론트엔드에서 8개 답변을 json으로 받아오는 (answerDTO의 리스트)
@Getter @Setter
public class OnboardingAnswerRequestDTO {
    private List<AnswerDTO> answers;
}
