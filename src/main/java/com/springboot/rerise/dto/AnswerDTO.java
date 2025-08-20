package com.springboot.rerise.dto;

import lombok.Getter;
import lombok.Setter;

//개별 질문의 응답을 담음
@Getter @Setter
public class AnswerDTO {
    private int questionNumber;
    private int selectedOption;
}
