package com.springboot.rerise.service;

import com.springboot.rerise.dto.AnswerDTO;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserMissionProfile;
import com.springboot.rerise.repository.UserMissionProfileRepository;
import com.springboot.rerise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MissionProfileService {

    @Autowired
    private UserMissionProfileRepository userMissionProfileRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 온보딩 답변을 기반으로 사용자 미션 프로필을 생성합니다.
     */
    @Transactional
    public UserMissionProfile createUserMissionProfile(Long userId, List<AnswerDTO> answers) {
        
        // 기존 프로필이 있다면 삭제
        userMissionProfileRepository.findByUserUserId(userId).ifPresent(existingProfile -> {
            userMissionProfileRepository.delete(existingProfile);
        });

        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
        
        // 새로운 프로필 생성 (기본값 1.0)
        UserMissionProfile profile = new UserMissionProfile();
        profile.setUser(user);  // User 객체 설정 (userId는 자동으로 설정됨)
        
        // 온보딩 답변 기반 가중치 계산
        calculateWeights(profile, answers);
        
        return userMissionProfileRepository.save(profile);
    }

    /**
     * 온보딩 답변을 분석하여 가중치를 계산합니다.
     */
    private void calculateWeights(UserMissionProfile profile, List<AnswerDTO> answers) {
        
        for (AnswerDTO answer : answers) {
            int questionNumber = answer.getQuestionNumber();
            int selectedOption = answer.getSelectedOption();
            
            switch (questionNumber) {
                case 1: // 현재 상태/성향
                    applyQuestion1Weights(profile, selectedOption);
                    break;
                case 2: // 휴식 방식
                    applyQuestion2Weights(profile, selectedOption);
                    break;
                case 3: // 에너지 소모 상황
                    applyQuestion3Weights(profile, selectedOption);
                    break;
                case 5: // 선호 장소
                    applyQuestion5Weights(profile, selectedOption);
                    break;
                case 6: // 원하는 변화
                    applyQuestion6Weights(profile, selectedOption);
                    break;
                case 8: // 심리적 허들
                    applyQuestion8Weights(profile, selectedOption);
                    break;
            }
        }
    }
    
    private void applyQuestion1Weights(UserMissionProfile profile, int selectedOption) {
        switch (selectedOption) {
            case 1: // 현관문 (모니)
                profile.setBehavioralActivationWeight(profile.getBehavioralActivationWeight() + 0.3);
                break;
            case 2: // 쉼터 벤치 (토리) 
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.3);
                break;
            case 3: // 오솔길 (포리)
                profile.setGratitudePracticeWeight(profile.getGratitudePracticeWeight() + 0.3);
                break;
            case 4: // 등산로 (코코)
                profile.setCognitiveRestructuringWeight(profile.getCognitiveRestructuringWeight() + 0.3);
                break;
        }
    }
    
    private void applyQuestion2Weights(UserMissionProfile profile, int selectedOption) {
        switch (selectedOption) {
            case 1: // 혼자만의 시간
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.4);
                break;
            case 2: // 취미활동
                profile.setBehavioralActivationWeight(profile.getBehavioralActivationWeight() + 0.4);
                break;
            case 3: // 소소한 경험
                profile.setSocialConnectionWeight(profile.getSocialConnectionWeight() + 0.4);
                break;
            case 4: // 미뤄뒀던 정리
                profile.setBehavioralActivationWeight(profile.getBehavioralActivationWeight() + 0.2);
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.2);
                break;
        }
    }
    
    private void applyQuestion3Weights(UserMissionProfile profile, int selectedOption) {
        switch (selectedOption) {
            case 1: // 낯선 사람 소통
                profile.setSocialConnectionWeight(profile.getSocialConnectionWeight() - 0.3);
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.3);
                break;
            case 2: // 시끄러운 공간
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.4);
                break;
            case 3: // 예상치 못한 일
                profile.setCognitiveRestructuringWeight(profile.getCognitiveRestructuringWeight() + 0.4);
                break;
            case 4: // 고립감
                profile.setSocialConnectionWeight(profile.getSocialConnectionWeight() + 0.4);
                break;
        }
    }
    
    private void applyQuestion5Weights(UserMissionProfile profile, int selectedOption) {
        switch (selectedOption) {
            case 1: // 목적 분명한 곳
                profile.setCognitiveRestructuringWeight(profile.getCognitiveRestructuringWeight() + 0.3);
                break;
            case 2: // 자연/탁 트인 곳
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.4);
                break;
            case 3: // 아늑하고 조용한 곳
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.3);
                profile.setGratitudePracticeWeight(profile.getGratitudePracticeWeight() + 0.1);
                break;
            case 4: // 활기찬 곳
                profile.setBehavioralActivationWeight(profile.getBehavioralActivationWeight() + 0.4);
                break;
        }
    }
    
    private void applyQuestion6Weights(UserMissionProfile profile, int selectedOption) {
        switch (selectedOption) {
            case 1: // 성취감/활력
                profile.setBehavioralActivationWeight(profile.getBehavioralActivationWeight() + 0.4);
                break;
            case 2: // 새로운 발견
                profile.setGratitudePracticeWeight(profile.getGratitudePracticeWeight() + 0.4);
                break;
            case 3: // 감정 기록
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.4);
                break;
            case 4: // 웃을 일
                profile.setGratitudePracticeWeight(profile.getGratitudePracticeWeight() + 0.4);
                break;
        }
    }
    
    private void applyQuestion8Weights(UserMissionProfile profile, int selectedOption) {
        switch (selectedOption) {
            case 1: // 작심삼일
                profile.setBehavioralActivationWeight(profile.getBehavioralActivationWeight() + 0.3);
                break;
            case 2: // 타인 의식
                profile.setMindfulnessWeight(profile.getMindfulnessWeight() + 0.3);
                break;
            case 3: // 방향성 부재
                profile.setCognitiveRestructuringWeight(profile.getCognitiveRestructuringWeight() + 0.3);
                break;
            case 4: // 효과 의심
                profile.setGratitudePracticeWeight(profile.getGratitudePracticeWeight() + 0.3);
                break;
        }
    }
}