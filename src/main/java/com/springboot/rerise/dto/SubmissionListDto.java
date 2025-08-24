package com.springboot.rerise.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class SubmissionListDto {
    private Long userProofMissionId;
    private String nickname;
    private String email;
    private LocalDate submissionDate;
}
