package com.springboot.rerise.dto;

import com.springboot.rerise.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class DailyRecordDto {
    private Long record_id;
    //private User user;
    private Integer emotion_level;
    private List<String> keywords;
    private String memo;
    private LocalDate recordedAt;
}