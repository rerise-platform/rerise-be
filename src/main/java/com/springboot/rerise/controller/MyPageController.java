package com.springboot.rerise.controller;

import com.springboot.rerise.dto.MyPageResponseDto;
import com.springboot.rerise.service.MyPageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    @GetMapping
    public ResponseEntity<MyPageResponseDto.CharacterResponse> ShowCharacter(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = (String) authentication.getPrincipal();
        MyPageResponseDto.CharacterResponse mainInfo = myPageService.showCharacter(email);

        return ResponseEntity.ok(mainInfo);
    }
    @GetMapping("/info")
    public ResponseEntity<MyPageResponseDto.InfoResponse> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = (String) authentication.getPrincipal();
        MyPageResponseDto.InfoResponse userInfo = myPageService.getUserInfo(email);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/info/update")
    public ResponseEntity<MyPageResponseDto.InfoResponse> updateMyProfile(
            @Valid @RequestBody MyPageResponseDto.ProfileUpdateRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        String userEmail = (String) authentication.getPrincipal();

        MyPageResponseDto.InfoResponse updatedInfo = myPageService.updateProfile(userEmail, request);
        return ResponseEntity.ok(updatedInfo);
    }

    @PatchMapping("/settings")
    public ResponseEntity<MyPageResponseDto.InfoResponse> updateMyNotificationSettings(
            @Valid @RequestBody MyPageResponseDto.NotificationSettingsRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String userEmail = (String) authentication.getPrincipal();

        MyPageResponseDto.InfoResponse updatedInfo = myPageService.updateNotificationSettings(userEmail, request);
        return ResponseEntity.ok(updatedInfo);
    }
}
