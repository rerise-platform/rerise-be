package com.springboot.rerise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user_characters")
public class UserCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_character_id")
    private Long userCharacterId;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Characters character;

    @Builder.Default
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Builder.Default
    @Column(name = "character_stage", nullable = false)
    private Integer stage = 1;

    @Builder.Default
    @Column(name = "experience", nullable = false)
    private Integer experience = 0;

    @Builder.Default
    @Column(name = "point", nullable = false)
    private Integer point = 0;

    @Builder.Default
    @Column(name = "obtained_date", nullable = false)
    private LocalDateTime obtainedDate = LocalDateTime.now();
}