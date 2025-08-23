package com.springboot.rerise.repository;

import com.springboot.rerise.entity.UserCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Integer> {
}
