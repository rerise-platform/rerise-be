package com.springboot.rerise.repository;

import com.springboot.rerise.entity.UserDailyMissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserDailyMissionsRepository extends JpaRepository<UserDailyMissions, Long> {
    
    @Query("SELECT udm FROM UserDailyMissions udm WHERE udm.user.userId = :userId AND udm.assignedDate = :date")
    List<UserDailyMissions> findByUserIdAndAssignedDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT udm FROM UserDailyMissions udm WHERE udm.user.userId = :userId AND udm.assignedDate = :date AND udm.status = 'PENDING'")
    List<UserDailyMissions> findPendingMissionsByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT CASE WHEN COUNT(udm) > 0 THEN true ELSE false END FROM UserDailyMissions udm WHERE udm.user.userId = :userId AND udm.assignedDate = :assignedDate")
    boolean existsByUserUserIdAndAssignedDate(@Param("userId") Long userId, @Param("assignedDate") LocalDate assignedDate);
}