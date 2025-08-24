package com.springboot.rerise.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor // 접근 제어자를 public으로 변경
@Table(name = "daily_record")
public class DailyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long record_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "emotion_level", nullable = false)
    private int emotion_level;

    @Column(name = "memo")
    private String memo;

    @ElementCollection
    @CollectionTable(name = "keywords", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Column(name = "recorded_at", nullable = false)
    private LocalDate recordedAt;
}
