package com.springboot.rerise.controller;

import com.springboot.rerise.dto.UserCharacterDto;

import com.springboot.rerise.entity.UserCharacter;
import com.springboot.rerise.repository.UserRepository;
import com.springboot.rerise.service.CharacterGrowthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class testController {
    @Autowired
    CharacterGrowthService characterGrowthService;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/api/v1/{userId}/addExp")
    public ResponseEntity<UserCharacterDto> addExp(@PathVariable Long userId
            , @RequestBody UserCharacterDto characterDto) {
        UserCharacter userCharacter = characterGrowthService.updateUserExperience(userId, characterDto.getExperience());
        characterDto = new UserCharacterDto(userCharacter);
        return ResponseEntity.ok(characterDto);
    }
}
