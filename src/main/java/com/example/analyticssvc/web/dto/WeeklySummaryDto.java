package com.example.analyticssvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySummaryDto {
    private double avgCaloriesConsumed;
    private double avgWaterIntake;
    private double avgCaloriesBurned;
    private int totalWorkouts;
    private int totalAchievements;
    private List<AchievementDto> recentAchievements;
}
