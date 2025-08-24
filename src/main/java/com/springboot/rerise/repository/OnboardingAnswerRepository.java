package com.springboot.rerise.repository;

import com.springboot.rerise.entity.OnboardingAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingAnswerRepository extends JpaRepository<OnboardingAnswer, Long> {
    // 특정 사용자의 모든 답변을 찾는 메서드
     List<OnboardingAnswer> findByUserId(Long userId);
}