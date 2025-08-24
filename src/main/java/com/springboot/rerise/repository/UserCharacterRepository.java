package com.springboot.rerise.repository;

import com.springboot.rerise.entity.UserCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Integer> {
    Optional<UserCharacter> findByUserUserId(Long userId);
}
