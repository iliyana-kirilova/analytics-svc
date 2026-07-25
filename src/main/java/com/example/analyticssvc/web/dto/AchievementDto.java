package com.example.analyticssvc.web.dto;

import com.example.analyticssvc.model.achievment.AchievementStatus;
import com.example.analyticssvc.model.achievment.AchievementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    private UUID id;
    private UUID userId;
    private AchievementType achievementType;
    private LocalDateTime unlockedAt;
    private AchievementStatus status;
}
