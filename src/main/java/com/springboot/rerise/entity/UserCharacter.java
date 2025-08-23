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

    // --- [수정 시작] ---
    // 유저 한 명당 캐릭터는 하나만 존재하므로 @ManyToOne을 @OneToOne으로 변경합니다.
    @OneToOne(fetch = FetchType.LAZY)
    // OneToOne 관계에서는 데이터베이스 레벨에서도 유일성을 보장하기 위해 unique = true를 추가하는 것이 좋습니다.
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    // --- [수정 끝] ---

    // 이 관계는 여러 유저가 동일한 종류의 기본 캐릭터(Characters)를 가질 수 있으므로 ManyToOne이 맞습니다.
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