package com.springboot.rerise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_mission_profile")
public class UserMissionProfile {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "mindfulness_weight", nullable = false)
    private Double mindfulnessWeight = 1.0;
    
    @Column(name = "behavioral_activation_weight", nullable = false) 
    private Double behavioralActivationWeight = 1.0;
    
    @Column(name = "cognitive_restructuring_weight", nullable = false)
    private Double cognitiveRestructuringWeight = 1.0;
    
    @Column(name = "social_connection_weight", nullable = false)
    private Double socialConnectionWeight = 1.0;
    
    @Column(name = "gratitude_practice_weight", nullable = false)
    private Double gratitudePracticeWeight = 1.0;
    
    @Column(name = "preferred_difficulty_start", nullable = false)
    private Integer preferredDifficultyStart = 1;
    
    @Column(name = "energy_recovery_focus", nullable = false)
    private Boolean energyRecoveryFocus = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}