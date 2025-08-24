package com.springboot.rerise.config;

import com.springboot.rerise.entity.Characters;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.CharacterRepository; // CharacterRepository 임포트
import com.springboot.rerise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 연결할 기본 캐릭터가 있는지 확인하고, 없으면 생성
        Characters defaultCharacter = characterRepository.findById(1L).orElseGet(() -> {
            Characters newChar = new Characters();

            return characterRepository.save(newChar);
        });

        // 관리자 계정 생성 (이미 있다면 실행 안됨)
        String adminEmail = "admin@rerise.com";
        if (!userRepository.findByEmail(adminEmail).isPresent()) {

            // UserCharacter 객체 생성
            UserCharacter adminCharacter = new UserCharacter();
            adminCharacter.setLevel(1);
            adminCharacter.setExperience(0);
            adminCharacter.setCharacter(defaultCharacter);

            // User 객체 생성
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin1234"))
                    .nickname("관리자")
                    .role(UserRole.ADMIN)
                    .build();

            admin.setUserCharacter(adminCharacter);
            adminCharacter.setUser(admin);

            // User를 저장
            userRepository.save(admin);
            System.out.println("초기 관리자 계정 및 캐릭터 정보가 생성되었습니다: " + adminEmail);
        }
    }
}