package com.example.analyticssvc.repository;

import com.example.analyticssvc.model.DailySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySnapshotRepository extends JpaRepository<DailySnapshot, UUID> {
    Optional<DailySnapshot> findByUserIdAndSnapshotDate(UUID userId, LocalDate date);


    List<DailySnapshot> findByUserIdAndSnapshotDateAfter(UUID userId, LocalDate localDate);
}
