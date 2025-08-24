package com.springboot.rerise.repository;

import com.springboot.rerise.entity.UserProofMissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProofMissionsRepository extends JpaRepository<UserProofMissions, Long> {
    @Query("SELECT upm FROM UserProofMissions upm WHERE upm.user.userId = :userId AND upm.mission.missionId = :missionId")
    Optional<UserProofMissions> findByUserAndMissionId(@Param("userId") Long userId, @Param("missionId") Long missionId);
    List<UserProofMissions> findByStatus(UserProofMissions.WeeklyMissionStatus status);
}
