package com.springboot.rerise.service;

import com.springboot.rerise.dto.DailyMissionResponseDTO;
import com.springboot.rerise.entity.*;
import com.springboot.rerise.repository.DailyRecordRepository;
import com.springboot.rerise.repository.MissionsRepository;
import com.springboot.rerise.repository.UserDailyMissionsRepository;
import com.springboot.rerise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DailyMissionService {
    
    private final MissionsRepository missionsRepository;
    private final UserDailyMissionsRepository userDailyMissionsRepository;
    private final UserRepository userRepository;
    private final DailyRecordRepository dailyRecordRepository;
    private final GeminiService geminiService;
    private final CharacterGrowthService characterGrowthService;
    
    public List<DailyMissionResponseDTO> generateDailyMissions(Long userId, String userInput) {
        log.info("=== Daily Mission Generation Started ===");
        log.info("User ID: {}, User Input: {}", userId, userInput);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        LocalDate today = LocalDate.now();
        log.info("Today: {}", today);
        
        boolean existsToday = userDailyMissionsRepository.existsByUserUserIdAndAssignedDate(userId, today);
        log.info("Missions already exist for today: {}", existsToday);
        
        if (existsToday) {
            List<DailyMissionResponseDTO> todayMissions = getTodayMissions(userId);
            log.info("Returning existing missions count: {}", todayMissions.size());
            return todayMissions;
        }
        
        String recentDiaryContext = getRecentDiaryContext(user);
        
        List<String> themes = geminiService.analyzeUserState(userInput, recentDiaryContext);
        String theoryStr = geminiService.getRecommendedTheory(themes);
        Missions.MissionTheory theory = Missions.MissionTheory.valueOf(theoryStr);
        
        int userLevel = getUserLevel(user);
        
        List<Missions> selectedMissions = selectMissions(themes, theory, userLevel);
        
        List<UserDailyMissions> userDailyMissions = saveDailyMissions(user, selectedMissions, today);
        
        return userDailyMissions.stream()
            .map(DailyMissionResponseDTO::from)
            .collect(Collectors.toList());
    }
    
    public List<DailyMissionResponseDTO> getTodayMissions(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserDailyMissions> missions = userDailyMissionsRepository
            .findByUserIdAndAssignedDate(userId, today);
        
        return missions.stream()
            .map(DailyMissionResponseDTO::from)
            .collect(Collectors.toList());
    }
    
    public DailyMissionResponseDTO completeMission(Long userId, Long userDailyMissionId) {
        UserDailyMissions mission = userDailyMissionsRepository.findById(userDailyMissionId)
            .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다."));
        
        if (!mission.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        if (mission.getStatus() == UserDailyMissions.MissionStatus.COMPLETED) {
            throw new RuntimeException("이미 완료된 미션입니다.");
        }
        
        mission.completeMission();

        characterGrowthService.updateUserExperience(userId, mission.getMission().getRewardExp());
        //updateUserExperience(userId, mission.getMission().getRewardExp());
        
        userDailyMissionsRepository.save(mission);
        
        return DailyMissionResponseDTO.from(mission);
    }
    
    private List<Missions> selectMissions(List<String> themes, Missions.MissionTheory theory, int userLevel) {
        log.info("=== Mission Selection Started ===");
        log.info("Themes: {}, Theory: {}, User Level: {}", themes, theory, userLevel);
        
        List<Missions> selectedMissions = new ArrayList<>();
        
        List<Missions> themeMissions = missionsRepository.findByThemesAndLevelTier(themes, userLevel);
        log.info("Found {} theme missions for level <= {}", themeMissions.size(), userLevel);
        
        // 전체 미션 개수 확인
        List<Missions> allMissions = missionsRepository.findAll();
        log.info("Total missions in database: {}", allMissions.size());
        
        // Level 1 미션 개수 확인  
        List<Missions> level1Missions = missionsRepository.findByLevelTier(1);
        log.info("Level 1 missions in database: {}", level1Missions.size());
        
        Collections.shuffle(themeMissions);
        
        int regularMissionsNeeded = 4;
        for (int i = 0; i < Math.min(regularMissionsNeeded, themeMissions.size()); i++) {
            selectedMissions.add(themeMissions.get(i));
        }
        
        int whileLoopCount = 0;
        while (selectedMissions.size() < regularMissionsNeeded && whileLoopCount < 10) {
            whileLoopCount++;
            
            List<Missions> fallbackMissions = missionsRepository.findByLevelTier(userLevel);
            Collections.shuffle(fallbackMissions);
            
            boolean added = false;
            for (Missions mission : fallbackMissions) {
                if (!selectedMissions.contains(mission)) {
                    selectedMissions.add(mission);
                    added = true;
                    break;
                }
            }
            
            if (!added) {
                List<Missions> allMissionsForFallback = missionsRepository.findAllRandomly();
                for (Missions mission : allMissionsForFallback) {
                    if (!selectedMissions.contains(mission)) {
                        selectedMissions.add(mission);
                        break;
                    }
                }
            }
        }
        
        if (whileLoopCount >= 10) {
            log.warn("미션 선택에서 최대 시도 횟수에 도달했습니다. 현재 선택된 미션: {}", selectedMissions.size());
        }
        
        List<Missions> specialMissions = missionsRepository.findByTheoryAndLevelTier(theory, userLevel);
        if (!specialMissions.isEmpty()) {
            Collections.shuffle(specialMissions);
            boolean found = false;
            for (Missions mission : specialMissions) {
                if (!selectedMissions.contains(mission)) {
                    selectedMissions.add(mission);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                List<Missions> allSpecialMissions = missionsRepository.findAllRandomly();
                for (Missions mission : allSpecialMissions) {
                    if (!selectedMissions.contains(mission) && mission.getTheory() == theory) {
                        selectedMissions.add(mission);
                        break;
                    }
                }
            }
        }
        
        return selectedMissions.subList(0, Math.min(5, selectedMissions.size()));
    }
    
    private List<UserDailyMissions> saveDailyMissions(User user, List<Missions> missions, LocalDate date) {
        List<UserDailyMissions> userDailyMissions = new ArrayList<>();
        
        for (Missions mission : missions) {
            UserDailyMissions userDailyMission = new UserDailyMissions();
            userDailyMission.setUser(user);
            userDailyMission.setMission(mission);
            userDailyMission.setAssignedDate(date);
            userDailyMission.setStatus(UserDailyMissions.MissionStatus.PENDING);
            
            userDailyMissions.add(userDailyMissionsRepository.save(userDailyMission));
        }
        
        return userDailyMissions;
    }
    
    private int getUserLevel(User user) {
        if (user.getUserCharacter() != null) {
            return user.getUserCharacter().getLevel();
        }
        return 1;
    }


    private String getRecentDiaryContext(User user) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        
        List<DailyRecord> recentRecords = dailyRecordRepository
            .findByUserAndRecordedAtBetween(user, weekAgo, today);
        
        if (recentRecords.isEmpty()) {
            return "최근 일기 기록이 없습니다.";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("최근 7일간의 감정 패턴과 일기 내용: ");
        
        for (DailyRecord record : recentRecords) {
            context.append(String.format(
                "[%s] 감정점수: %d점", 
                record.getRecordedAt(), 
                record.getEmotion_level()
            ));
            
            if (record.getMemo() != null && !record.getMemo().trim().isEmpty()) {
                context.append(", 메모: ").append(record.getMemo().substring(0, Math.min(50, record.getMemo().length())));
            }
            
            if (record.getKeywords() != null && !record.getKeywords().isEmpty()) {
                context.append(", 키워드: ").append(String.join(", ", record.getKeywords()));
            }
            
            context.append("; ");
        }
        
        return context.toString();
    }
}