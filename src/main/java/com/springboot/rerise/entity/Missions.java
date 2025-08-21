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
@Table(name = "missions")
public class Missions {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long missionId;
    
    @Column(name = "content", nullable = false, length = 500)
    private String content;
    
    @Column(name = "theme", nullable = false, length = 50)
    private String theme;
    
    @Column(name = "level_tier", nullable = false)
    private int levelTier;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "theory", nullable = false)
    private MissionTheory theory;
    
    @Column(name = "reward_exp", nullable = false)
    private int rewardExp = 10;
    
    public enum MissionTheory {
        BEHAVIORAL_ACTIVATION,
        COGNITIVE_RESTRUCTURING, 
        MINDFULNESS,
        SOCIAL_CONNECTION,
        GRATITUDE_PRACTICE
    }
}