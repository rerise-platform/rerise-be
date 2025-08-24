package com.springboot.rerise.dto;
/*
 * AI API와의 대화에서 메시지 단위를 나타내는 클래스.
 * 사용자의 질문(user)이나 AI의 답변(assistant), 또는 페르소나 설정(system)과 같은
 * 메시지의 '역할(role)'과 실제 '내용(content)'을 담는 데 사용됩니다.
 */
// Lombok을 사용하여 Getter, Setter 등을 자동으로 생성합니다.
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;
    private String content;
}