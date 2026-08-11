package com.example.analyticssvc.util;

import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import com.example.analyticssvc.web.dto.ChallengeRequest;
import com.example.analyticssvc.model.challenge.ChallengeType;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AnalyticsFactory {

    public static AchievementCheckRequest getMealAddedRequest(UUID userId) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .eventType("MEAL_ADDED")
                .caloriesConsumed(500)
                .consecutiveDays(1)
                .completeDayFlag(false)
                .build();
    }

    public static AchievementCheckRequest getWaterGoalRequest(UUID userId) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .waterIntake(2500)
                .targetWater(2000)
                .build();
    }

    public static AchievementCheckRequest getCalorieGoalRequest(UUID userId) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .targetCalories(2000)
                .caloriesConsumed(1900)
                .build();
    }

    public static AchievementCheckRequest getBurnedCaloriesRequest(UUID userId) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .totalCaloriesBurned(1000)
                .build();
    }

    public static AchievementCheckRequest getCompleteDayRequest(UUID userId) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .completeDayFlag(true)
                .build();
    }

    public static ChallengeRequest getChallengeRequest(UUID userId) {
        return ChallengeRequest.builder()
                .userId(userId)
                .challengeType(ChallengeType.WORKOUT_5_TIMES)
                .build();
    }

    public static AchievementCheckRequest getMacroBalanceRequest(UUID userId) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .targetProtein(150.0)
                .proteinConsumed(140.0)
                .targetCarbs(200.0)
                .carbsConsumed(185.0)
                .targetFats(70.0)
                .fatsConsumed(65.0)
                .build();
    }

    public static AchievementCheckRequest getWorkoutProgressRequest(UUID userId, int workoutStreakDays) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .workoutStreakDays(workoutStreakDays)
                .build();
    }

    public static AchievementCheckRequest getWaterProgressRequest(UUID userId, int waterIntake) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .waterIntake(waterIntake)
                .build();
    }

    public static AchievementCheckRequest getConsecutiveDaysProgressRequest(UUID userId, int consecutiveDays) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .consecutiveDays(consecutiveDays)
                .build();
    }

    public static AchievementCheckRequest getBurnedCaloriesProgressRequest(UUID userId, int caloriesBurned) {
        return AchievementCheckRequest.builder()
                .userId(userId)
                .caloriesBurned(caloriesBurned)
                .build();
    }
}