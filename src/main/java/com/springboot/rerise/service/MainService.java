package com.springboot.rerise.service;

import com.springboot.rerise.dto.DailyMissionResponseDTO;
import com.springboot.rerise.dto.MainResponseDTO;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MainService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CharacterGrowthService characterGrowthService;

    @Autowired
    private DailyMissionService dailyMissionService;

    public MainResponseDTO getMainInfo(String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        // UserCharacter 확인
        UserCharacter userCharacter = user.getUserCharacter();
        
        if (userCharacter == null) {
            // 온보딩을 완료하지 않은 사용자 - 데일리미션만 조회
            List<DailyMissionResponseDTO> dailyMissions = dailyMissionService.getTodayMissions(user.getUserId());
            return new MainResponseDTO(
                user.getNickname(),
                null,
                null,
                null,
                null,
                dailyMissions
            );
        }

        // 캐릭터 정보 추출
        String characterType = userCharacter.getCharacter().getCharacterType();
        Integer characterStage = userCharacter.getStage();
        Integer level = userCharacter.getLevel();
        
        // 성장률 계산
        Double growthRate = calculateGrowthRate(userCharacter);

        // 데일리 미션 조회
        List<DailyMissionResponseDTO> dailyMissions = dailyMissionService.getTodayMissions(user.getUserId());

        return new MainResponseDTO(
            user.getNickname(),
            characterType,
            characterStage,
            level,
            growthRate,
            dailyMissions
        );
    }

    private Double calculateGrowthRate(UserCharacter userCharacter) {
        int currentLevel = userCharacter.getLevel();
        int currentExp = userCharacter.getExperience();
        
        // 현재 레벨의 시작 경험치
        int currentLevelStartExp = characterGrowthService.getTotalExpForLevel(currentLevel);
        
        // 다음 레벨까지 필요한 경험치
        int nextLevelRequiredExp = characterGrowthService.getTotalExpForLevel(currentLevel + 1);
        
        // 현재 레벨에서 다음 레벨까지의 구간
        int levelExpRange = nextLevelRequiredExp - currentLevelStartExp;
        
        // 현재 레벨에서 얼마나 진행했는지
        int progressInCurrentLevel = currentExp - currentLevelStartExp;
        
        // 성장률 계산 (0.0 ~ 100.0)
        if (levelExpRange == 0) {
            return 100.0; // 최고 레벨인 경우
        }
        
        double growthRate = (double) progressInCurrentLevel / levelExpRange * 100.0;
        
        // 0~100 범위로 제한
        return Math.max(0.0, Math.min(100.0, growthRate));
    }
}