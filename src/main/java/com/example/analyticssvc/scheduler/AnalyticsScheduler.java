package com.example.analyticssvc.scheduler;

import com.example.analyticssvc.repository.UserAchievementRepository;
import com.example.analyticssvc.repository.UserChallengeRepository;
import com.example.analyticssvc.model.challenge.ChallengeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Component
public class AnalyticsScheduler implements SchedulingConfigurer {

    private final UserChallengeRepository challengeRepository;
    private final UserAchievementRepository achievementRepository;

    public AnalyticsScheduler(UserChallengeRepository challengeRepository,
                              UserAchievementRepository achievementRepository) {
        this.challengeRepository = challengeRepository;
        this.achievementRepository = achievementRepository;
    }


    @Scheduled(cron = "0 0 1 * * *")
    public void expireOverdueChallenges() {
        var active = challengeRepository
                .findAllByStatus(ChallengeStatus.ACTIVE);

        long expired = active.stream()
                .filter(c -> c.getDeadline().isBefore(LocalDateTime.now()))
                .peek(c -> {
                    c.setStatus(ChallengeStatus.ABANDONED);
                    challengeRepository.save(c);
                })
                .count();

        log.info("[SCHEDULER - CRON] Expired {} overdue challenges.", expired);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::logAnalyticsStats,
                new Trigger() {
                    @Override
                    public Instant nextExecution(TriggerContext context) {
                        Instant lastExecution = context.lastActualExecution();
                        if (lastExecution == null) {
                            return Instant.now();
                        }
                        // Добавяме 12 часа към последното изпълнение
                        return lastExecution.plus(Duration.ofHours(12));
                    }
                }
        );
    }

    private void logAnalyticsStats() {
        long achievements = achievementRepository.count();
        long challenges = challengeRepository.count();
        log.info("[SCHEDULER - TRIGGER] Analytics stats: {} achievements, {} challenges",
                achievements, challenges);
    }
}