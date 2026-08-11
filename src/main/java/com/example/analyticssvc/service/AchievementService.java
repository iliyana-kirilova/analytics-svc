package com.example.analyticssvc.service;

import com.example.analyticssvc.model.DailySnapshot;
import com.example.analyticssvc.model.achievment.AchievementStatus;
import com.example.analyticssvc.model.achievment.UserAchievement;
import com.example.analyticssvc.repository.DailySnapshotRepository;
import com.example.analyticssvc.repository.UserAchievementRepository;
import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import com.example.analyticssvc.web.dto.AchievementDto;
import com.example.analyticssvc.web.dto.DailySnapshotRequest;
import com.example.analyticssvc.web.dto.WeeklySummaryDto;
import com.example.analyticssvc.web.mapper.AchievementMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementService {
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementChecker checker;
    private final DailySnapshotRepository dailySnapshotRepository;

    public AchievementService(UserAchievementRepository achievementRepository,
                              AchievementChecker checker, DailySnapshotRepository dailySnapshotRepository) {
        this.userAchievementRepository = achievementRepository;
        this.checker = checker;
        this.dailySnapshotRepository = dailySnapshotRepository;
    }

    @CacheEvict(value = "userAchievements", key = "#request.userId")
    public List<AchievementDto> checkAndSave(AchievementCheckRequest request) {
        return checker.check(request)
                .stream()
                .map(AchievementMapper::toDto)
                .toList();
    }

    @Cacheable(value = "userAchievements", key = "#userId")
    public List<AchievementDto> getUserAchievements(UUID userId) {
        return userAchievementRepository
                .findByUserIdAndStatus(userId, AchievementStatus.ACTIVE)
                .stream()
                .map(AchievementMapper::toDto)
                .toList();
    }

    public List<AchievementDto> getAllAchievements() {
        return userAchievementRepository
                .findAllByStatus(AchievementStatus.ACTIVE)
                .stream()
                .map(AchievementMapper::toDto)
                .toList();
    }

    public void archiveAchievement(UUID id, UUID userId) {
        UserAchievement achievement = userAchievementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Achievement not found: " + id));

        if (!achievement.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        achievement.setStatus(AchievementStatus.ARCHIVED);
        userAchievementRepository.save(achievement);
    }

    public void deleteAchievement(UUID id) {
        userAchievementRepository.deleteById(id);
    }

    public WeeklySummaryDto getWeeklySummary(UUID userId) {

        List<DailySnapshot> snapshots = dailySnapshotRepository
                .findByUserIdAndSnapshotDateAfter(
                        userId, LocalDate.now().minusDays(7));

        double avgCalories = snapshots.stream()
                .mapToInt(DailySnapshot::getCaloriesConsumed)
                .average().orElse(0);

        double avgWater = snapshots.stream()
                .mapToInt(DailySnapshot::getWaterIntake)
                .average().orElse(0);

        double avgBurned = snapshots.stream()
                .mapToInt(DailySnapshot::getCaloriesBurned)
                .average().orElse(0);

        List<AchievementDto> recent = userAchievementRepository
                .findByUserId(userId)
                .stream()
                .filter(a -> a.getUnlockedAt()
                        .isAfter(LocalDateTime.now().minusDays(7)))
                .map(AchievementMapper::toDto)
                .toList();

        return WeeklySummaryDto.builder()
                .totalAchievements(recent.size())
                .recentAchievements(recent)
                .avgCaloriesConsumed(avgCalories)
                .avgWaterIntake(avgWater)
                .avgCaloriesBurned(avgBurned)
                .build();
    }

    public void recordSnapshot(DailySnapshotRequest request) {
        // Upsert — ако вече има за днес, обновява
        dailySnapshotRepository
                .findByUserIdAndSnapshotDate(request.getUserId(), LocalDate.now())
                .ifPresentOrElse(
                        existing -> {
                            existing.setCaloriesConsumed(request.getCaloriesConsumed());
                            existing.setWaterIntake(request.getWaterIntake());
                            existing.setCaloriesBurned(request.getCaloriesBurned());
                            dailySnapshotRepository.save(existing);
                        },
                        () -> dailySnapshotRepository.save(DailySnapshot.builder()
                                .userId(request.getUserId())
                                .caloriesConsumed(request.getCaloriesConsumed())
                                .waterIntake(request.getWaterIntake())
                                .caloriesBurned(request.getCaloriesBurned())
                                .snapshotDate(LocalDate.now())
                                .build())
                );
    }



}
