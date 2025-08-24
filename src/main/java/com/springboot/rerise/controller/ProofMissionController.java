package com.springboot.rerise.controller;

import com.springboot.rerise.dto.*;
import com.springboot.rerise.entity.User;
import com.springboot.rerise.service.ProofMissionService;
import com.springboot.rerise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProofMissionController {

    private final ProofMissionService proofMissionService;
    private final UserService userService;

    // 사용자가 인증 자료를 제출하는 엔드포인트
    @PostMapping("/missions/weekly/submit")
    public ResponseEntity<String> submitProof(
            @AuthenticationPrincipal String email,
            @RequestBody ProofSubmitRequestDto requestDto) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        proofMissionService.submitProof(user.getUser_id(), requestDto);
        return ResponseEntity.ok("인증 자료가 성공적으로 제출되었습니다. 관리자의 승인을 기다려주세요.");
    }
    //승인 대기 중인 미션 목록 조회
    @GetMapping("/admin/submissions")
    public ResponseEntity<List<SubmissionListDto>> getPendingSubmissions() {
        return ResponseEntity.ok(proofMissionService.getPendingSubmissions());
    }

    //승인 대기 중인 미션 상세 조회
    @GetMapping("/admin/submissions/{userProofMissionId}")
    public ResponseEntity<SubmissionDetailDto> getSubmissionDetails(@PathVariable Long userProofMissionId) {
        return ResponseEntity.ok(proofMissionService.getSubmissionDetails(userProofMissionId));
    }

    // 관리자가 미션을 승인/거절
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/approve")
    public ResponseEntity<Object> approveMission(
            @RequestBody ProofApprovalRequestDto approvalDto) {

        MissionRewardResponseDto responseDto = proofMissionService.approveMission(approvalDto);
        if (responseDto == null) {
            // 미션이 거절된 경우
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "미션이 거절되었습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok(responseDto);
    }
}