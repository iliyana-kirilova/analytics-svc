package com.example.analyticssvc.model.achievment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_achievement")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType achievementType;

    @Column(nullable = false)
    private LocalDateTime unlockedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementStatus status;
}
