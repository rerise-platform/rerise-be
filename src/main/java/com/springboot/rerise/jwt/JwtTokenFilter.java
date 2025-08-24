package com.springboot.rerise.jwt;

import com.springboot.rerise.entity.User;
import com.springboot.rerise.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final String secretKey;
    private final JwtTokenBlocklist jwtTokenBlocklist;

    public JwtTokenFilter(UserService userService,
                          @Value("${springboot.jwt.secret}") String secretKey,
                          JwtTokenBlocklist jwtTokenBlocklist) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.jwtTokenBlocklist = jwtTokenBlocklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지 않음
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
        String token = authorizationHeader.split(" ")[1];

        if (jwtTokenBlocklist.isBlocklisted(token)) {
            // 토큰이 차단 목록에 있으면, 인증을 거부하고 다음 필터로 진행
            // HTTP 401 Unauthorized를 직접 반환할 수도 있지만, 필터 체인에 따라 처리
            filterChain.doFilter(request, response);
            return;
        }
        // 전송받은 Jwt Token이 만료되었으면 => 다음 필터 진행(인증 X)
        if (JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Jwt Token에서 email 추출
        String email = JwtTokenUtil.getLoginId(token, secretKey);
        String role = JwtTokenUtil.getRole(token, secretKey);
        
        // role이 null이거나 빈 문자열인 경우 기본값 설정
        if (role == null || role.trim().isEmpty()) {
            role = "ROLE_USER";
        }

        // 추출한 email로 User 찾기
        User loginUser = userService.getLoginUserByLoginId(email);

        // loginUser 정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority(role)));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
