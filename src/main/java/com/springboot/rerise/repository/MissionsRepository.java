package com.springboot.rerise.repository;

import com.springboot.rerise.entity.Missions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionsRepository extends JpaRepository<Missions, Long> {
    
    @Query("SELECT m FROM Missions m WHERE m.theme IN :themes AND m.levelTier <= :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByThemesAndLevelTier(@Param("themes") List<String> themes, @Param("maxLevel") int maxLevel);
    
    @Query("SELECT m FROM Missions m WHERE m.theory = :theory AND m.levelTier <= :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByTheoryAndLevelTier(@Param("theory") Missions.MissionTheory theory, @Param("maxLevel") int maxLevel);
    
    @Query("SELECT m FROM Missions m WHERE m.levelTier <= :maxLevel ORDER BY FUNCTION('RAND')")
    List<Missions> findByLevelTier(@Param("maxLevel") int maxLevel);
    
    @Query("SELECT m FROM Missions m ORDER BY FUNCTION('RAND')")
    List<Missions> findAllRandomly();
}