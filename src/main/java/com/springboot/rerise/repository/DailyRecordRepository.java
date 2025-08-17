package com.springboot.rerise.repository;

import com.springboot.rerise.entity.DailyRecord;
import com.springboot.rerise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
    List<DailyRecord> findByUserAndRecordedAtBetween(User user, LocalDate startDate, LocalDate endDate);
    Optional<DailyRecord> findByUserAndRecordedAt(User user, LocalDate date);
    boolean existsByUserAndRecordedAt(User user, LocalDate date);
}
