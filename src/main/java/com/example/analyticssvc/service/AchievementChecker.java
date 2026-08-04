package com.example.analyticssvc.service;

import com.example.analyticssvc.model.achievment.AchievementStatus;
import com.example.analyticssvc.model.achievment.AchievementType;
import com.example.analyticssvc.model.achievment.UserAchievement;
import com.example.analyticssvc.repository.UserAchievementRepository;
import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AchievementChecker {

    private final UserAchievementRepository userAchievementRepository;

    public AchievementChecker(UserAchievementRepository userAchievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
    }


    public List<UserAchievement> check(AchievementCheckRequest request) {
        List<UserAchievement> newAchievements = new ArrayList<>();
        UUID userId = request.getUserId();
        String event = request.getEventType();

        //First record
        checkAndUnlock(userId, AchievementType.FIRST_MEAL_LOGGED,
                "MEAL_ADDED".equals(event), newAchievements);

        checkAndUnlock(userId, AchievementType.FIRST_WORKOUT_LOGGED,
                "WORKOUT_ADDED".equals(event), newAchievements);

        checkAndUnlock(userId, AchievementType.FIRST_LOG_CREATED,
                "LOG_CREATED".equals(event), newAchievements);

        //Water
        boolean waterGoalMet = request.getWaterIntake() != null
                && request.getTargetWater() != null
                && request.getTargetWater() > 0
                && request.getWaterIntake() >= request.getTargetWater();

        checkAndUnlock(userId, AchievementType.DAILY_WATER_GOAL_MET,
                waterGoalMet, newAchievements);

        checkAndUnlock(userId, AchievementType.WATER_STREAK_3,
                request.getWaterStreakDays() != null
                        && request.getWaterStreakDays() >= 3,
                newAchievements);

        checkAndUnlock(userId, AchievementType.HYDRATION_MASTER,
                request.getWaterStreakDays() != null
                        && request.getWaterStreakDays() >= 7,
                newAchievements);

        //Calories
        checkAndUnlock(userId, AchievementType.CALORIE_GOAL_MET,
                request.getCaloriesConsumed() != null
                        && request.getTargetCalories() != null
                        && request.getTargetCalories() > 0
                        && request.getCaloriesConsumed() <= request.getTargetCalories()
                        && request.getCaloriesConsumed()
                        >= request.getTargetCalories() * 0.9,
                newAchievements);

        //Macros
        checkAndUnlock(userId, AchievementType.MACRO_BALANCE_MASTER,
                request.getProteinConsumed() != null
                        && request.getTargetProtein() != null
                        && request.getCarbsConsumed() != null
                        && request.getTargetCarbs() != null
                        && request.getFatsConsumed() != null
                        && request.getTargetFats() != null
                        && request.getTargetProtein() > 0
                        && request.getTargetCarbs() > 0
                        && request.getTargetFats() > 0
                        && request.getProteinConsumed() >= request.getTargetProtein() * 0.9
                        && request.getCarbsConsumed() >= request.getTargetCarbs() * 0.9
                        && request.getFatsConsumed() >= request.getTargetFats() * 0.9,
                newAchievements);

        //Workout
        checkAndUnlock(userId, AchievementType.BURNED_1000_CALORIES,
                request.getTotalCaloriesBurned() != null
                        && request.getTotalCaloriesBurned() >= 1000,
                newAchievements);

        checkAndUnlock(userId, AchievementType.WORKOUT_STREAK_3,
                request.getWorkoutStreakDays() != null
                        && request.getWorkoutStreakDays() >= 3,
                newAchievements);

        checkAndUnlock(userId, AchievementType.WORKOUT_STREAK_7,
                request.getWorkoutStreakDays() != null
                        && request.getWorkoutStreakDays() >= 7,
                newAchievements);

        //Logs
        checkAndUnlock(userId, AchievementType.STREAK_3_DAYS,
                request.getConsecutiveDays() != null
                        && request.getConsecutiveDays() >= 3,
                newAchievements);

        checkAndUnlock(userId, AchievementType.STREAK_7_DAYS,
                request.getConsecutiveDays() != null
                        && request.getConsecutiveDays() >= 7,
                newAchievements);

        checkAndUnlock(userId, AchievementType.STREAK_30_DAYS,
                request.getConsecutiveDays() != null
                        && request.getConsecutiveDays() >= 30,
                newAchievements);

        //Complete day
        checkAndUnlock(userId, AchievementType.COMPLETE_DAY,
                Boolean.TRUE.equals(request.getCompleteDayFlag()),
                newAchievements);

        return newAchievements;
    }

    private void checkAndUnlock(UUID userId,
                                AchievementType type,
                                boolean condition,
                                List<UserAchievement> results) {
        if (condition
                && !userAchievementRepository.existsByUserIdAndAchievementType(userId, type)) {
            UserAchievement achievement = UserAchievement.builder()
                    .userId(userId)
                    .achievementType(type)
                    .unlockedAt(LocalDateTime.now())
                    .status(AchievementStatus.ACTIVE)
                    .build();
            results.add(userAchievementRepository.save(achievement));
        }
    }
}
