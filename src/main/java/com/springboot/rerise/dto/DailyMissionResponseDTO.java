package com.springboot.rerise.dto;

import com.springboot.rerise.entity.Missions;
import com.springboot.rerise.entity.UserDailyMissions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyMissionResponseDTO {
    
    private Long userDailyMissionId;
    private Long missionId;
    private String content;
    private String theme;
    private Missions.MissionTheory theory;
    private int rewardExp;
    private UserDailyMissions.MissionStatus status;
    private LocalDate assignedDate;
    private LocalDateTime completedAt;
    
    public static DailyMissionResponseDTO from(UserDailyMissions userDailyMission) {
        return new DailyMissionResponseDTO(
            userDailyMission.getUserDailyMissionId(),
            userDailyMission.getMission().getMissionId(),
            userDailyMission.getMission().getContent(),
            userDailyMission.getMission().getTheme(),
            userDailyMission.getMission().getTheory(),
            userDailyMission.getMission().getRewardExp(),
            userDailyMission.getStatus(),
            userDailyMission.getAssignedDate(),
            userDailyMission.getCompletedAt()
        );
    }
}