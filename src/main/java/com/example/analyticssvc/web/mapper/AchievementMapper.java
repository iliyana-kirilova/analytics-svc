package com.example.analyticssvc.web.mapper;

import com.example.analyticssvc.model.achievment.UserAchievement;
import com.example.analyticssvc.web.dto.AchievementDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AchievementMapper {
    public static AchievementDto toDto(UserAchievement achievement) {
        if (achievement == null) {
            return null;
        }
        return AchievementDto.builder()
                .id(achievement.getId())
                .userId(achievement.getUserId())
                .achievementType(achievement.getAchievementType())
                .unlockedAt(achievement.getUnlockedAt())
                .status(achievement.getStatus())
                .build();
    }

    public static UserAchievement toEntity(AchievementDto dto) {
        if (dto == null) {
            return null;
        }
        return UserAchievement.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .achievementType(dto.getAchievementType())
                .unlockedAt(dto.getUnlockedAt())
                .status(dto.getStatus())
                .build();
    }
}
