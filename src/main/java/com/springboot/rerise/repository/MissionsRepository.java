package com.springboot.rerise.repository;

import com.springboot.rerise.entity.Missions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionsRepository extends JpaRepository<Missions, Long> {
    
    @Query("SELECT m FROM Missions m WHERE m.theme IN :themes AND m.missionLevel <= :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByThemesAndLevelTier(@Param("themes") List<String> themes, @Param("maxLevel") int maxLevel);
    
    @Query("SELECT m FROM Missions m WHERE m.theory = :theory AND m.missionLevel <= :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByTheoryAndLevelTier(@Param("theory") Missions.MissionTheory theory, @Param("maxLevel") int maxLevel);
    
    @Query("SELECT m FROM Missions m WHERE m.missionLevel <= :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByLevelTier(@Param("maxLevel") int maxLevel);
    
    @Query("SELECT m FROM Missions m ORDER BY FUNCTION('RAND')")
    List<Missions> findAllRandomly();
    
    // 주간 미션 생성을 위한 추가 메소드들
    @Query("SELECT m FROM Missions m WHERE m.theory = :theory AND m.theme IN :themes AND m.missionLevel BETWEEN :minLevel AND :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByTheoryAndThemeInAndMissionLevelBetween(
        @Param("theory") Missions.MissionTheory theory, 
        @Param("themes") List<String> themes, 
        @Param("minLevel") int minLevel, 
        @Param("maxLevel") int maxLevel
    );
    
    @Query("SELECT m FROM Missions m WHERE m.theme IN :themes AND m.missionLevel BETWEEN :minLevel AND :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByThemeInAndMissionLevelBetween(
        @Param("themes") List<String> themes, 
        @Param("minLevel") int minLevel, 
        @Param("maxLevel") int maxLevel
    );
    
    @Query("SELECT m FROM Missions m WHERE m.missionLevel BETWEEN :minLevel AND :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByMissionLevelBetween(
        @Param("minLevel") int minLevel, 
        @Param("maxLevel") int maxLevel
    );
}