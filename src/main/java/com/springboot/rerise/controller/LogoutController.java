package com.springboot.rerise.controller;

import com.springboot.rerise.jwt.JwtTokenBlocklist;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LogoutController {

    private final JwtTokenBlocklist jwtTokenBlocklist;

    public LogoutController(JwtTokenBlocklist jwtTokenBlocklist) {
        this.jwtTokenBlocklist = jwtTokenBlocklist;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // HTTP 요청 헤더에서 Authorization 값을 가져옵니다.
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 헤더가 존재하고 "Bearer "로 시작하는지 확인합니다.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " 부분을 제외하고 토큰만 추출
            jwtTokenBlocklist.addToken(token); // 토큰을 차단 목록에 추가
            return ResponseEntity.ok("로그아웃 성공. 토큰이 서버에서 무효화되었습니다.");
        }

        return ResponseEntity.badRequest().body("유효한 Authorization 헤더가 없습니다.");
    }
}