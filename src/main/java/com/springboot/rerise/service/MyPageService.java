package com.springboot.rerise.service;

import com.springboot.rerise.dto.MyPageResponseDto;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Service
public class MyPageService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MainService mainService;

    public MyPageResponseDto.CharacterResponse showCharacter(String email){
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        String nickname = user.getNickname();
        // UserCharacter 확인
        UserCharacter userCharacter = user.getUserCharacter();

        // 캐릭터 정보 추출
        String characterType = userCharacter.getCharacter().getCharacterType();
        Integer characterStage = userCharacter.getStage();
        Integer level = userCharacter.getLevel();

        // 성장률 계산
        Double growthRate = mainService.calculateGrowthRate(userCharacter);

        return new MyPageResponseDto.CharacterResponse(
                nickname,
                characterType,
                characterStage,
                level,
                growthRate
        );
    }

    public MyPageResponseDto.InfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return new MyPageResponseDto.InfoResponse(user);
    }

    @Transactional
    public MyPageResponseDto.InfoResponse updateProfile(String email, MyPageResponseDto.ProfileUpdateRequest profileUpdateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        user.updateUser(profileUpdateRequest.getNickname(), profileUpdateRequest.getBirth());

        return new MyPageResponseDto.InfoResponse(user);
    }

    @Transactional
    public MyPageResponseDto.InfoResponse updateNotificationSettings(String email, MyPageResponseDto.NotificationSettingsRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        user.updateNotificationSettings(
                request.getActivityPushEnabled(),
                request.getProgressSmsEnabled(),
                request.getMoodCheckEnabled()
        );
        return new MyPageResponseDto.InfoResponse(user);
    }

}
