package com.springboot.rerise.dto;

import lombok.Getter;

@Getter
public class ProofApprovalRequestDto {
    private Long userProofMissionId;
    private boolean isApproved;
}
