package com.springboot.rerise.service;

import com.springboot.rerise.dto.AnswerDTO;
import com.springboot.rerise.dto.OnboardingResultResponseDTO;
import com.springboot.rerise.entity.Characters;
import com.springboot.rerise.entity.OnboardingAnswer;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.CharacterRepository;
import com.springboot.rerise.repository.OnboardingAnswerRepository;
import com.springboot.rerise.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OnboardingService {

    @Autowired
    private OnboardingAnswerRepository onboardingAnswerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CharacterRepository characterRepository;
    @Autowired
    private UserService userService;


    // 가중치 Map<질문번호, Map<선택지번호, 타입>>
    private static final Map<Integer, Map<Integer, String>> typeMapping = new HashMap<>();

    static {
        // 예시: 1번 질문에서 1번 답변 선택 시 "TYPE_A", 2번 답변 선택 시 "TYPE_B"
        Map<Integer, String> optionMap = Map.of(
                1, "TYPE_A",
                2, "TYPE_B",
                3, "TYPE_C",
                4, "TYPE_D"
        );
        // 1번~8번 질문에 동일한 매핑 적용
        for (int i = 1; i <= 8; i++) {
            typeMapping.put(i, optionMap);
        }
    }

    public OnboardingResultResponseDTO processOnboardingAnswers(List<AnswerDTO> answers) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        String email = (String) authentication.getPrincipal();
        User currentUser = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        Long userId = currentUser.getUser_id();

        //1. 캐릭터별 점수 계산
        Map<String, Integer> characterScores = new HashMap<>();
        characterScores.put("TYPE_A", 0);
        characterScores.put("TYPE_B", 0);
        characterScores.put("TYPE_C", 0);
        characterScores.put("TYPE_D", 0);

        for (AnswerDTO answer : answers) {
            int questionNumber = answer.getQuestionNumber();
            int selectedOption = answer.getSelectedOption();

            String type = typeMapping.get(questionNumber).get(selectedOption);
            characterScores.put(type, characterScores.get(type) + 1);

        }

        // 2. 최고점으로 캐릭터 결정
        String highestType = characterScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("TYPE_A"); // 기본값

        Characters characters = characterRepository.findByCharacterType(highestType);
        if (characters == null) {
            // 해당 타입의 캐릭터가 DB에 없는 경우 예외 처리
            throw new IllegalArgumentException("Character type not found in database: " + highestType);
        }
        // 3. OnboardingAnswer 테이블에 답변 저장
        for (AnswerDTO answer : answers) {
            OnboardingAnswer entity = new OnboardingAnswer();
            entity.setUserId(userId);
            entity.setQuestionNumber(answer.getQuestionNumber());
            entity.setSelectedOption(answer.getSelectedOption());
            onboardingAnswerRepository.save(entity);
        }


        // 4. User 테이블에 캐릭터 업데이트
        // 1. 사용자의 UserCharacter 객체를 가져옵니다. 만약 없다면 새로 생성합니다.
        UserCharacter userCharacter = currentUser.getUserCharacter();
        if (userCharacter == null) {
            userCharacter = new UserCharacter();
        }

        // 2. UserCharacter 객체에 필요한 정보를 설정합니다.
        userCharacter.setCharacter(characters); // 결정된 캐릭터 타입(Characters) 설정
        userCharacter.setUser(currentUser); // UserCharacter와 User의 양방향 관계 설정

        // 3. User 엔티티에 완성된 UserCharacter를 설정합니다.
        currentUser.setUserCharacter(userCharacter);

        // 4. User를 저장합니다. Cascade 설정에 따라 UserCharacter도 함께 저장/업데이트됩니다.
        userRepository.save(currentUser);


        // 5. 결과 DTO 반환
        return getResultDTO(userId, highestType);
    }


    private OnboardingResultResponseDTO getResultDTO(Long userId, String characterType) {
        // 1. DB에서 characterType에 해당하는 Characters 엔티티를 조회
        Characters character = characterRepository.findByCharacterType(characterType);

        if (character == null) {
            throw new EntityNotFoundException("Character type not found in database: " + characterType);
        }

        // 2. 조회된 엔티티 데이터를 DTO에 매핑
        OnboardingResultResponseDTO resultDTO = new OnboardingResultResponseDTO();
        resultDTO.setUserId(userId);
        resultDTO.setCharacterId(character.getCharacter_id());
        resultDTO.setCharacterType(character.getCharacterType());
        resultDTO.setDescription(character.getDescription());
        resultDTO.setKeywords(List.of(character.getKeyword1(), character.getKeyword2(), character.getKeyword3()));
        resultDTO.setEnergyLevel(character.getEnergyLevel());
        resultDTO.setAdaptability(character.getAdaptability());
        resultDTO.setResilience(character.getResilience());

        return resultDTO;
    }
}