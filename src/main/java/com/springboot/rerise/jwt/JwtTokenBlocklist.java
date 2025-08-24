package com.springboot.rerise.jwt;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtTokenBlocklist {
    // 로그아웃된 토큰을 저장하는 Set입니다.
    // Thread-safe한 Set을 사용합니다.
    private final Set<String> blocklistedTokens = Collections.synchronizedSet(new HashSet<>());

    /**
     * 토큰을 차단 목록에 추가합니다.
     *
     * @param token 로그아웃할 토큰 문자열
     */
    public void addToken(String token) {
        blocklistedTokens.add(token);
    }

    /**
     * 토큰이 차단 목록에 있는지 확인합니다.
     *
     * @param token 확인할 토큰 문자열
     * @return 토큰이 차단 목록에 있으면 true, 아니면 false
     */
    public boolean isBlocklisted(String token) {
        return blocklistedTokens.contains(token);
    }
}