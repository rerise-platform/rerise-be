package com.springboot.rerise.dto;
/*
 * Perplexity AI API에 보낼 요청 본문(Request Body)을 정의하는 클래스.
 * 어떤 언어 모델을 사용할지(model)와 대화 내용을 담은 메시지 목록(messages)을 API로 전송할 때 사용됩니다.
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerplexityRequest {
    private String model;
    private List<Message> messages;
}