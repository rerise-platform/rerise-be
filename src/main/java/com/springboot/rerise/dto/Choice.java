package com.springboot.rerise.dto;
/*
 * Perplexity AI API 응답에서 'choices' 배열의 각 요소를 매핑하는 클래스.
 * API가 생성한 여러 답변 후보 중 하나를 나타냅니다.
 * 이 클래스는 Lombok의 @Data 어노테이션을 사용하여 getter, setter, toString 등을 자동 생성합니다.
 */
import lombok.Data;

@Data
public class Choice {
    private int index;
    private Message message;
    // finish_reason 등 다른 필드도 필요하면 추가할 수 있습니다.
}