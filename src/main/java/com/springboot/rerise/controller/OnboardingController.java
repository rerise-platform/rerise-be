package com.springboot.rerise.controller;

import com.springboot.rerise.dto.OnboardingAnswerRequestDTO;
import com.springboot.rerise.dto.OnboardingResultResponseDTO;
import com.springboot.rerise.service.OnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @PostMapping("/complete")
    public ResponseEntity<OnboardingResultResponseDTO> completeOnboarding(
            @RequestBody OnboardingAnswerRequestDTO requestDTO) {

        // Remove the userId argument from the method call
        OnboardingResultResponseDTO resultDTO = onboardingService.processOnboardingAnswers(requestDTO.getAnswers());

        return ResponseEntity.ok(resultDTO);
    }
}