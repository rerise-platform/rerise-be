package com.springboot.rerise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.rerise.entity.Characters;

@Repository
public interface CharacterRepository extends JpaRepository<Characters, Long> {
    Characters findByCharacterType(String type);
}