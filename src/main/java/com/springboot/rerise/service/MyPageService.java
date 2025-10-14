package com.springboot.rerise.service;

import com.springboot.rerise.dto.MyPageResponseDto;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MainService mainService;

    public MyPageResponseDto showCharacter(String email){
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        // UserCharacter 확인
        UserCharacter userCharacter = user.getUserCharacter();

        // 캐릭터 정보 추출
        String characterType = userCharacter.getCharacter().getCharacterType();
        Integer characterStage = userCharacter.getStage();
        Integer level = userCharacter.getLevel();

        // 성장률 계산
        Double growthRate = mainService.calculateGrowthRate(userCharacter);

        return new MyPageResponseDto(
                user.getNickname(),
                characterType,
                characterStage,
                level,
                growthRate
        );
    }

}
