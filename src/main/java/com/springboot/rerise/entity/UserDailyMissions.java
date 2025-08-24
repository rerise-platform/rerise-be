package com.springboot.rerise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_daily_missions")
public class UserDailyMissions {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_daily_mission_id")
    private Long userDailyMissionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Missions mission;
    
    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MissionStatus status = MissionStatus.PENDING;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum MissionStatus {
        PENDING,
        COMPLETED,
        EXPIRED
    }
    
    public void completeMission() {
        this.status = MissionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}