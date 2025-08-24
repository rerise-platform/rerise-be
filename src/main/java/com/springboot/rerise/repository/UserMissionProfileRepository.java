package com.springboot.rerise.repository;

import com.springboot.rerise.entity.UserMissionProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMissionProfileRepository extends JpaRepository<UserMissionProfile, Long> {
    
    Optional<UserMissionProfile> findByUserUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}