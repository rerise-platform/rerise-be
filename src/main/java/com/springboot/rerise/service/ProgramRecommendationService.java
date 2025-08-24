package com.springboot.rerise.service;

import com.springboot.rerise.entity.Program;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramRecommendationService {

    private final UserService userService;
    private List<Program> allPrograms = new ArrayList<>();

    /**
     * 애플리케이션 시작 시 CSV 파일을 로드합니다
     */
    @PostConstruct
    public void loadProgramsFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("young_enterprise.csv");
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                boolean isFirstLine = true;
                
                while ((line = reader.readLine()) != null) {
                    // 첫 번째 줄(헤더) 건너뛰기
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    // 빈 줄 건너뛰기
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    Program program = parseCsvLine(line);
                    if (program != null) {
                        allPrograms.add(program);
                    }
                }
                
                log.info("총 {}개의 프로그램을 로드했습니다.", allPrograms.size());
                
            }
        } catch (IOException e) {
            log.error("CSV 파일을 읽는 중 오류가 발생했습니다: {}", e.getMessage());
        }
    }

    /**
     * CSV 한 줄을 파싱하여 Program 객체로 변환
     */
    private Program parseCsvLine(String line) {
        try {
            String[] fields = parseCSVFields(line);
            
            if (fields.length >= 6) {
                return new Program(
                    fields[0].trim(),  // category
                    fields[1].trim(),  // program_name
                    fields[2].trim(),  // target
                    fields[3].trim(),  // recruitment_period
                    fields[4].trim(),  // location
                    fields[5].trim()   // url
                );
            }
        } catch (Exception e) {
            log.warn("CSV 라인 파싱 중 오류: {}", line, e);
        }
        return null;
    }

    /**
     * CSV 필드 파싱 (쉼표로 구분하되 따옴표 내의 쉼표는 무시)
     */
    private String[] parseCSVFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * 사용자에게 맞는 프로그램 3개 추천
     */
    public List<Program> getRecommendedPrograms() {
        User currentUser = getCurrentUser();
        UserCharacter userCharacter = currentUser.getUserCharacter();
        
        if (userCharacter == null) {
            // 온보딩을 완료하지 않은 사용자는 문화 프로그램 추천
            return getRandomCulturePrograms(3);
        }
        
        int userLevel = userCharacter.getLevel();
        
        // 레벨 기반 추천 로직
        if (userLevel >= 10) {
            // 높은 레벨: 취업/커리어 프로그램 우선 추천
            return getCareerFocusedPrograms(3);
        } else {
            // 낮은 레벨: 문화 프로그램 우선 추천
            return getCultureFocusedPrograms(3);
        }
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("사용자가 로그인되어 있지 않습니다.");
        }
        
        String email = (String) authentication.getPrincipal();
        return userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * 커리어 중심 프로그램 추천 (청년 프로그램 우선)
     */
    private List<Program> getCareerFocusedPrograms(int count) {
        List<Program> youthPrograms = allPrograms.stream()
                .filter(Program::isYouthProgram)
                .collect(Collectors.toList());
        
        Collections.shuffle(youthPrograms);
        
        if (youthPrograms.size() >= count) {
            return youthPrograms.subList(0, count);
        } else {
            // 청년 프로그램이 부족하면 문화 프로그램으로 채우기
            List<Program> result = new ArrayList<>(youthPrograms);
            List<Program> culturePrograms = getRandomCulturePrograms(count - youthPrograms.size());
            result.addAll(culturePrograms);
            return result;
        }
    }

    /**
     * 문화 중심 프로그램 추천
     */
    private List<Program> getCultureFocusedPrograms(int count) {
        List<Program> culturePrograms = allPrograms.stream()
                .filter(Program::isCultureProgram)
                .collect(Collectors.toList());
        
        Collections.shuffle(culturePrograms);
        
        if (culturePrograms.size() >= count) {
            return culturePrograms.subList(0, count);
        } else {
            // 문화 프로그램이 부족하면 청년 프로그램으로 채우기
            List<Program> result = new ArrayList<>(culturePrograms);
            List<Program> youthPrograms = getRandomYouthPrograms(count - culturePrograms.size());
            result.addAll(youthPrograms);
            return result;
        }
    }

    /**
     * 랜덤 문화 프로그램 조회
     */
    private List<Program> getRandomCulturePrograms(int count) {
        List<Program> culturePrograms = allPrograms.stream()
                .filter(Program::isCultureProgram)
                .collect(Collectors.toList());
        
        Collections.shuffle(culturePrograms);
        return culturePrograms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * 랜덤 청년 프로그램 조회
     */
    private List<Program> getRandomYouthPrograms(int count) {
        List<Program> youthPrograms = allPrograms.stream()
                .filter(Program::isYouthProgram)
                .collect(Collectors.toList());
        
        Collections.shuffle(youthPrograms);
        return youthPrograms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
