package com.springboot.rerise.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionDetailDto {
    private Long userId;
    private String nickname;
    private String email;

    private Long userProofMissionId;
    private String missionContent;
    private String proofData;
    private LocalDateTime submissionDate;
}
