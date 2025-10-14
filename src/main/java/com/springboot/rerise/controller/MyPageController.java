package com.springboot.rerise.controller;

import com.springboot.rerise.dto.MyPageResponseDto;
import com.springboot.rerise.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponseDto> ShowCharacter(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = (String) authentication.getPrincipal();
        MyPageResponseDto mainInfo = myPageService.showCharacter(email);

        return ResponseEntity.ok(mainInfo);
    }
}
