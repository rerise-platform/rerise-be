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
@Table(name = "user_characters")
public class UserCharacter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_character_id")
    private Long userCharacterId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Characters character;
    
    @Column(name = "level", nullable = false)
    private int level = 1;
    
    @Column(name = "experience", nullable = false)
    private int experience = 0;
    
    @Column(name = "obtained_date", nullable = false)
    private LocalDateTime obtainedDate = LocalDateTime.now();
}