package com.springboot.rerise.repository;

import com.springboot.rerise.entity.UserProofMissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProofMissionsRepository extends JpaRepository<UserProofMissions, Long> {
    Optional<UserProofMissions> findByUserIdAndMissionId(Long userId, Long missionId);
    List<UserProofMissions> findByStatus(UserProofMissions.WeeklyMissionStatus status);
}
