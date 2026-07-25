package com.example.analyticssvc.model.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeType {
    DRINK_3L_WATER("Drink 3L water daily for 7 days", 7),
    WORKOUT_5_TIMES("Work out 5 times this week", 7),
    HIT_CALORIE_GOAL_7_DAYS("Hit calorie goal 7 days in a row", 7),
    LOG_EVERY_DAY_MONTH("Log every day this month", 30),
    BURN_500_DAILY("Burn 500 kcal daily for 5 days", 5);

    private final String displayName;
    private final int durationDays;
}
