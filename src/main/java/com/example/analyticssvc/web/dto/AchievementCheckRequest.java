package com.example.analyticssvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementCheckRequest {
    private UUID userId;
    private String eventType;
    private Integer caloriesConsumed;
    private Integer targetCalories;
    private Integer waterIntake;
    private Integer targetWater;
    private Integer caloriesBurned;
    private Integer totalCaloriesBurned;
    private Integer consecutiveDays;
    private Double proteinConsumed;
    private Double targetProtein;
    private Double carbsConsumed;
    private Double targetCarbs;
    private Double fatsConsumed;
    private Double targetFats;
}
