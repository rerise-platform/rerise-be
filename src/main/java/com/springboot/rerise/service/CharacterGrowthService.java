package com.springboot.rerise.service;

import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.UserCharacterRepository;
import com.springboot.rerise.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CharacterGrowthService {
    private final UserRepository userRepository;
    private final UserCharacterRepository userCharacterRepository;

    @Autowired
    public CharacterGrowthService(UserRepository userRepository, UserCharacterRepository userCharacterRepository) {
        this.userRepository = userRepository;
        this.userCharacterRepository = userCharacterRepository;
    }

    @Transactional
    public UserCharacter updateUserExperience(Long userId, int gainExp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserCharacter userCharacter = user.getUserCharacter();
        if (userCharacter == null) {
            throw new IllegalStateException("UserCharacter not found for the user");
        }

        userCharacter.setExperience(userCharacter.getExperience() + gainExp);
        System.out.println("경험치 추가됨. 현재 경험치: " + userCharacter.getExperience());

        // 레벨업 및 진화 상태를 체크하고 업데이트합니다.
        checkLevelUp(userCharacter);
        checkEvolution(userCharacter);

        return userCharacterRepository.save(userCharacter);
        }

    private void checkLevelUp(UserCharacter userCharacter){
        boolean levelUp;
        do{
            levelUp = false;
            int nextLevel = userCharacter.getLevel() + 1;
            long requiredExp = getTotalExpForLevel(nextLevel);

            if(userCharacter.getExperience() >= requiredExp){
                userCharacter.setLevel(nextLevel);
                levelUp = true;
                System.out.println("Level up!: "+ userCharacter.getLevel());
            }
        }while(levelUp);
    }

    private void checkEvolution(UserCharacter userCharacter){
        int currentLevel = userCharacter.getLevel();
        Integer currentStageInteger = userCharacter.getStage(); // Integer로 먼저 받음

        // 만약 DB에서 가져온 stage 값이 null이면, 기본값 1로 처리
        if (currentStageInteger == null) {
            currentStageInteger = 1;
            userCharacter.setStage(currentStageInteger); // 객체의 상태도 업데이트
        }
        int currentStage = currentStageInteger;

        if(currentLevel >= 15 && currentStage < 2){
            userCharacter.setStage(2);
            System.out.println("캐릭터가 2단계로 진화했어요!");
        }
        if(currentLevel >= 30 && currentStage < 3){
            userCharacter.setStage(3);
            System.out.println("캐릭터가 3단계로 진화했어요!");
        }
    }

    //목표 레벨에 도달하기 위해 필요한 총 누적 경험치
    public long getTotalExpForLevel(int level){
        if (level <= 1) return 0;

        // Lv 2 ~ 10: 레벨당 100 XP
        if (level <= 10) {
            return (long) (level - 1) * 100;
        }
        // Lv 11 ~ 25: 레벨당 300 XP
        if (level <= 25) {
            // Lv 10까지의 누적 XP(1000) + 추가 XP
            return 1000 + (long) (level - 10) * 300;
        }
        // Lv 26 ~ 40: 레벨당 700 XP
        if (level <= 40) {
            // Lv 25까지의 누적 XP(5500) + 추가 XP
            return 5500 + (long) (level - 25) * 700;
        }
        // Lv 41 ~ 50: 레벨당 1500 XP
        // Lv 40까지의 누적 XP(16000) + 추가 XP
        return 16000 + (long) (level - 40) * 1500;
    }
}
