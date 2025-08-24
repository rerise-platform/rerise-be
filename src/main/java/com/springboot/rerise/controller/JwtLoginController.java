package com.springboot.rerise.controller;

import com.springboot.rerise.dto.JoinRequest;
import com.springboot.rerise.dto.LoginRequest;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.jwt.JwtTokenUtil;
import com.springboot.rerise.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JWT 로그인 API", description = "회원가입 및 로그인을 위한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class JwtLoginController {

    private final UserService userService;
    private static BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${springboot.jwt.secret}")
    private String secretKey;
    @Value("${springboot.jwt.expiration}")
    private long expireTimeMs;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패 (아이디/닉네임 중복, 비밀번호 불일치 등)")
    })
    @PostMapping("/signup")
    public String join(@RequestBody JoinRequest joinRequest) {
        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getEmail())) {
            return "로그인 아이디가 중복됩니다.";
        }
        // 닉네임 중복 체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            return "닉네임이 중복됩니다.";
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return "바밀번호가 일치하지 않습니다.";
        }
        userService.join(joinRequest);
        return "회원가입 성공";
    }

    @Operation(summary = "로그인", description = "로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 JWT 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "로그인 실패 (아이디 또는 비밀번호 불일치)")
    })
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        if(user == null) {
            return "로그인 아이디 또는 비밀번호가 틀렸습니다.";
        }

        String jwtToken = JwtTokenUtil.createToken(user.getEmail(), user.getRole(), secretKey, expireTimeMs);
        return jwtToken;
    }
}