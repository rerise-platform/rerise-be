package com.springboot.rerise.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionRewardResponseDto {
    private Long userId;
    private Long missionId;
    private boolean isCompleted;
    private String message;
    private int rewardedExp;
    private int rewardedPoint;
    private int userNewLevel;
    private int userNewTotalExp;
    private int userNewTotalPoint;

    @Builder
    public MissionRewardResponseDto(Long userId, Long missionId, boolean isCompleted, String message,
                                    int rewardedExp, int rewardedPoint, int userNewLevel, int userNewTotalExp, int userNewTotalPoint) {
        this.userId = userId;
        this.missionId = missionId;
        this.isCompleted = isCompleted;
        this.message = message;
        this.rewardedExp = rewardedExp;
        this.rewardedPoint = rewardedPoint;
        this.userNewLevel = userNewLevel;
        this.userNewTotalExp = userNewTotalExp;
        this.userNewTotalPoint = userNewTotalPoint;
    }
}