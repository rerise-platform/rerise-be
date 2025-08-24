package com.springboot.rerise.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 청년 및 문화 프로그램 정보를 담는 엔티티
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    
    /**
     * 프로그램 카테고리 (청년/문화)
     */
    private String category;
    
    /**
     * 프로그램명
     */
    private String programName;
    
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
     * 카테고리가 비어있는 경우 청년 프로그램으로 간주
     */
    public boolean isYouthProgram() {
        return category == null || category.trim().isEmpty();
    }
    
    /**
     * 문화 프로그램 여부 확인
     */
    public boolean isCultureProgram() {
        return "문화".equals(category);
    }
}
