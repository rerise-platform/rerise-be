package com.springboot.rerise.dto;
/*
 * Perplexity AI API의 전체 응답을 매핑하는 클래스.
 * API 호출 후 받아오는 JSON 데이터의 최상위 구조를 나타냅니다.
 */
import lombok.Data;
import java.util.List;

@Data
public class PerplexityResponse {
    private String id;
    private String model;
    private List<Choice> choices;
    // usage 등 다른 필드도 필요하면 추가할 수 있습니다.
}