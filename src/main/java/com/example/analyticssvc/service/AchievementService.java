package com.example.analyticssvc.service;

import com.example.analyticssvc.model.achievment.AchievementStatus;
import com.example.analyticssvc.model.achievment.UserAchievement;
import com.example.analyticssvc.repository.UserAchievementRepository;
import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import com.example.analyticssvc.web.dto.AchievementDto;
import com.example.analyticssvc.web.dto.WeeklySummaryDto;
import com.example.analyticssvc.web.mapper.AchievementMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementService {
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementChecker checker;

    public AchievementService(UserAchievementRepository achievementRepository,
                              AchievementChecker checker) {
        this.userAchievementRepository = achievementRepository;
        this.checker = checker;
    }

    public List<AchievementDto> checkAndSave(AchievementCheckRequest request) {
        return checker.check(request)
                .stream()
                .map(AchievementMapper::toDto)
                .toList();
    }

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
                .avgCaloriesConsumed(0)
                .avgWaterIntake(0)
                .avgCaloriesBurned(0)
                .build();
    }

}
