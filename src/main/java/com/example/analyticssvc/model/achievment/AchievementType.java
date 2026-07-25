package com.example.analyticssvc.model.achievment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AchievementType {
    FIRST_MEAL_LOGGED("First Meal Logged",
            "Log your first meal"),
    FIRST_WORKOUT_LOGGED("First Workout Logged",
            "Log your first workout"),
    FIRST_LOG_CREATED("First Log Created",
            "Create your first daily log"),
    CALORIE_GOAL_MET("Calorie Goal Met",
            "Stay within your calorie target for a day"),
    MACRO_BALANCE_MASTER("Macro Balance Master",
            "Hit all macro targets in one day"),
    DAILY_WATER_GOAL_MET("Hydration Goal Met",
            "Drink enough water for a full day"),
    WATER_STREAK_3("3-Day Hydration Streak",
            "Meet your water goal 3 days in a row"),
    HYDRATION_MASTER("Hydration Master",
            "Meet your water goal 7 days in a row"),
    BURNED_1000_CALORIES("Burn 1000 Calories",
            "Burn 1000 total calories from workouts"),
    WORKOUT_STREAK_3("3-Day Workout Streak",
            "Work out 3 days in a row"),
    WORKOUT_STREAK_7("7-Day Workout Streak",
            "Work out 7 days in a row"),
    STREAK_3_DAYS("3-Day Log Streak",
            "Log your daily data 3 days in a row"),
    STREAK_7_DAYS("7-Day Log Streak",
            "Log your daily data 7 days in a row"),
    STREAK_30_DAYS("30-Day Champion",
            "Log your daily data 30 days in a row"),
    COMPLETE_DAY("Complete Day",
            "Log meals, a workout and water in one day");


    private final String displayName;
    private final String hint;
}
