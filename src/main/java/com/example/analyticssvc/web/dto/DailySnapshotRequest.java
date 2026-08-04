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
public class DailySnapshotRequest {

    private UUID userId;
    private int caloriesConsumed;
    private int waterIntake;
    private int caloriesBurned;
}
