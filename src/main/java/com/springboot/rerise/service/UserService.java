package com.springboot.rerise.service;

import com.springboot.rerise.dto.JoinRequest;
import com.springboot.rerise.dto.LoginRequest;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * email 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkLoginIdDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입
     * 화면에서 JoinRequest(email, password, nickname)을 입력받아 User로 변환 후 저장
     * 회원가입 1과는 달리 비밀번호를 암호화해서 저장 (bCryptPasswordEncoder)
     * loginId, nickname 중복 체크는 Controller에서 진행 => 에러 메세지 출력을 위해
     */
    public void join(JoinRequest joinRequest) {
        userRepository.save(joinRequest.toEntity(bCryptPasswordEncoder.encode(joinRequest.getPassword())));
    }

    /**
     * 로그인 기능
     * 화면에서 LoginRequest(loginId, password)을 입력받아 loginId와 password가 일치하면 User return
     * loginId가 존재하지 않거나 password가 일치하지 않으면 null retur
     */
    public User login(LoginRequest req) {
        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());

// loginId와 일치하는 User가 없으면 null return
        if(optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(req.getPassword(), user.getPassword())) {
            return null; // 비밀번호가 틀림
        }
        return user;
    }



    /**
     * userId(Long)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * userId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * userId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserById(Long userId) {
        if(userId == null) return null;
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;
        return optionalUser.get();
    }

    /**
     * email(String)을 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * loginId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * loginId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserByLoginId(String email) {
        if(email == null) return null;

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();

    }

    /**
     * Spring Security - 필수 오버라이드 메서드
     * email을 입력받아 DB에서 회원 정보를 조회하여 UserDetails를 반환
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
// email로 사용자를 찾고 없으면 예외 처리
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}

