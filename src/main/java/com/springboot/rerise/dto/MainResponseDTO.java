package com.springboot.rerise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDTO {
    private String nickname;
    private String characterType;
    private Integer characterStage;
    private Integer level;
    private Double growthRate; // 성장률 (퍼센트, 0.0 ~ 100.0)
    private List<DailyMissionResponseDTO> dailyMissions;
}