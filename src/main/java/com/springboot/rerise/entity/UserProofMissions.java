package com.springboot.rerise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_weekly_missions")
public class UserProofMissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_mission_id")
    private Long userWeeklyMissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Missions mission;

    @Lob
    @Column(name = "proof_data")
    private String proofData; // 후기, 사진 URL 등

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WeeklyMissionStatus status = WeeklyMissionStatus.PROGRESS; // 상태 초기값

    @CreationTimestamp // 엔티티가 처음 저장될 때 자동으로 현재 시간을 기록
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;


    public enum WeeklyMissionStatus {
        PROGRESS,       // 진행 중
        PENDING,           // 승인 대기
        COMPLETED,         // 완료
        REJECTED           // 거절
    }
}