package com.example.analyticssvc.service;

import com.example.analyticssvc.model.challenge.ChallengeStatus;
import com.example.analyticssvc.model.challenge.ChallengeType;
import com.example.analyticssvc.model.challenge.UserChallenge;
import com.example.analyticssvc.repository.UserChallengeRepository;
import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import com.example.analyticssvc.web.dto.ChallengeDto;
import com.example.analyticssvc.web.dto.ChallengeRequest;
import com.example.analyticssvc.web.mapper.ChallengeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChallengeService {
    private final UserChallengeRepository challengeRepository;

    public ChallengeService(UserChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public ChallengeDto startChallenge(ChallengeRequest request) {
        if (challengeRepository.existsByUserIdAndChallengeTypeAndStatus(
                request.getUserId(),
                request.getChallengeType(),
                ChallengeStatus.ACTIVE)) {
            throw new RuntimeException("Challenge already active");
        }

        UserChallenge challenge = UserChallenge.builder()
                .userId(request.getUserId())
                .challengeType(request.getChallengeType())
                .startedAt(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(
                        request.getChallengeType().getDurationDays()))
                .status(ChallengeStatus.ACTIVE)
                .progressPercent(0)
                .build();

        return ChallengeMapper.toDto(challengeRepository.save(challenge));
    }

    public void abandonChallenge(UUID id, UUID userId) {
        UserChallenge challenge = challengeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Challenge not found: " + id));

        if (!challenge.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        challenge.setStatus(ChallengeStatus.ABANDONED);
        challengeRepository.save(challenge);
    }

    public void completeChallenge(UUID id) {
        UserChallenge challenge = challengeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Challenge not found: " + id));

        challenge.setStatus(ChallengeStatus.COMPLETED);
        challenge.setProgressPercent(100);
        challengeRepository.save(challenge);
    }

    public List<ChallengeDto> getUserActiveChallenges(UUID userId) {
        return challengeRepository
                .findByUserIdAndStatus(userId, ChallengeStatus.ACTIVE)
                .stream()
                .map(ChallengeMapper::toDto)
                .toList();
    }

    public List<ChallengeDto> getAllActiveChallenges() {
        return challengeRepository
                .findAllByStatus(ChallengeStatus.ACTIVE)
                .stream()
                .map(ChallengeMapper::toDto)
                .toList();
    }

    public void updateProgressForUser(UUID userId, AchievementCheckRequest request) {
        List<UserChallenge> active = challengeRepository
                .findByUserIdAndStatus(userId, ChallengeStatus.ACTIVE);

        for (UserChallenge challenge : active) {
            int newProgress = calculateProgress(challenge.getChallengeType(), request);
            if (newProgress > challenge.getProgressPercent()) {
                challenge.setProgressPercent(Math.min(newProgress, 100));
                challengeRepository.save(challenge);
            }
        }
    }

    private int calculateProgress(ChallengeType type, AchievementCheckRequest req) {
        return switch (type) {
            case WORKOUT_5_TIMES -> {
                int workouts = req.getWorkoutStreakDays() != null
                        ? req.getWorkoutStreakDays() : 0;
                yield Math.min(workouts * 20, 100);
            }
            case DRINK_3L_WATER -> {
                int water = req.getWaterIntake() != null ? req.getWaterIntake() : 0;
                yield Math.min((int) ((water / 3000.0) * 100), 100);
            }
            case HIT_CALORIE_GOAL_7_DAYS -> {
                int days = req.getConsecutiveDays() != null
                        ? req.getConsecutiveDays() : 0;
                yield Math.min(days * 14, 100);
            }
            case LOG_EVERY_DAY_MONTH -> {
                int days = req.getConsecutiveDays() != null
                        ? req.getConsecutiveDays() : 0;
                yield Math.min(days * 3, 100);
            }
            case BURN_500_DAILY -> {
                int burned = req.getCaloriesBurned() != null
                        ? req.getCaloriesBurned() : 0;
                yield Math.min((int) ((burned / 500.0) * 100), 100);
            }
        };

    }

    public void deleteChallenge(UUID id) {
        challengeRepository.deleteById(id);
    }
}
