package com.springboot.rerise.dto;

import com.springboot.rerise.entity.UserCharacter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCharacterDto {
    private Long userCharacterId;
    private Integer level;
    private Integer experience;
    private Integer stage;
    private String characterName; // 필요한 데이터만 선택적으로 포함

    public UserCharacterDto(UserCharacter entity) {
        this.userCharacterId = entity.getUserCharacterId();
        this.level = entity.getLevel();
        this.experience = entity.getExperience();
        this.stage = entity.getStage();
        this.characterName = entity.getCharacter().getName(); // 세션이 열려있을 때 실제 데이터 조회
    }
}
