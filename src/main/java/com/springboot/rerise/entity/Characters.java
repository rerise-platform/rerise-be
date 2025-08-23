package com.springboot.rerise.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="characters")
public class Characters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long character_id;

    @Column(name = "character_name")
    private String name;

    @Column(name = "character_type")
    private String characterType;

    @Column(name = "character_stage")
    private Integer stage;

    @Column(name = "character_description")
    private String description;

    @Column(name = "keyword1")
    private String keyword1;
    @Column(name = "keyword2")
    private String keyword2;
    @Column(name = "keyword3")
    private String keyword3;

    @Column(name = "energy")
    private Integer energyLevel;

    @Column(name = "adaptability")
    private Integer adaptability;

    @Column(name = "resilience")
    private Integer resilience;
}
