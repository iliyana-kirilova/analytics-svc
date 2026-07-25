package com.example.analyticssvc.repository;

import com.example.analyticssvc.model.achievment.AchievementStatus;
import com.example.analyticssvc.model.achievment.AchievementType;
import com.example.analyticssvc.model.achievment.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {


    List<UserAchievement> findByUserIdAndStatus(
            UUID userId, AchievementStatus status);

    List<UserAchievement> findAllByStatus(AchievementStatus status);

    boolean existsByUserIdAndAchievementType(
            UUID userId, AchievementType achievementType);

    List<UserAchievement> findByUserId(UUID userId);
}
