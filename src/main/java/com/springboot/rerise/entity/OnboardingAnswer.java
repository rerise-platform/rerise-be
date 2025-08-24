package com.springboot.rerise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "onboarding_answer")
public class OnboardingAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Column(name = "selected_option", nullable = false)
    private Integer selectedOption;
}
