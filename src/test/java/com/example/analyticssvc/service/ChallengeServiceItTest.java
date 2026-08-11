package com.example.analyticssvc.service;

import com.example.analyticssvc.model.challenge.ChallengeStatus;
import com.example.analyticssvc.model.challenge.ChallengeType;
import com.example.analyticssvc.repository.UserChallengeRepository;
import com.example.analyticssvc.util.AnalyticsFactory;
import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import com.example.analyticssvc.web.dto.ChallengeDto;
import com.example.analyticssvc.web.dto.ChallengeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

// Static imports за JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeServiceItTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private UserChallengeRepository challengeRepository;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    public void startChallenge_shouldSaveChallenge_withActiveStatus() {
        // Given (използваме AnalyticsFactory)
        ChallengeRequest request = AnalyticsFactory.getChallengeRequest(userId);

        // When
        ChallengeDto result = challengeService.startChallenge(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());

        assertEquals(ChallengeStatus.ACTIVE.name(), result.getStatus().toString());

        assertTrue(challengeRepository.existsByUserIdAndChallengeTypeAndStatus(
                userId, ChallengeType.WORKOUT_5_TIMES, ChallengeStatus.ACTIVE));
    }

    @Test
    public void startChallenge_whenAlreadyActive_shouldThrowException() {
        // Given
        ChallengeRequest request = AnalyticsFactory.getChallengeRequest(userId);
        challengeService.startChallenge(request);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> challengeService.startChallenge(request));
    }

    @Test
    public void abandonChallenge_shouldSetStatusToAbandoned() {
        // Given
        ChallengeRequest request = ChallengeRequest.builder()
                .userId(userId)
                .challengeType(ChallengeType.DRINK_3L_WATER)
                .build();

        ChallengeDto started = challengeService.startChallenge(request);

        // When
        challengeService.abandonChallenge(started.getId(), userId);

        // Then
        var challenge = challengeRepository.findById(started.getId()).orElseThrow();
        assertEquals(ChallengeStatus.ABANDONED, challenge.getStatus());
    }

    @Test
    public void abandonChallenge_whenNotFound_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class,
                () -> challengeService.abandonChallenge(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    public void abandonChallenge_whenUnauthorized_shouldThrowException() {
        // Given
        ChallengeRequest request = AnalyticsFactory.getChallengeRequest(userId);
        ChallengeDto started = challengeService.startChallenge(request);
        UUID otherUserId = UUID.randomUUID();

        // When & Then
        assertThrows(RuntimeException.class,
                () -> challengeService.abandonChallenge(started.getId(), otherUserId));
    }

    @Test
    public void completeChallenge_shouldSetStatusToCompletedAndProgressTo100() {
        // Given
        ChallengeRequest request = AnalyticsFactory.getChallengeRequest(userId);
        ChallengeDto started = challengeService.startChallenge(request);

        // When
        challengeService.completeChallenge(started.getId());

        // Then
        var challenge = challengeRepository.findById(started.getId()).orElseThrow();
        assertEquals(ChallengeStatus.COMPLETED, challenge.getStatus());
        assertEquals(100, challenge.getProgressPercent());
    }

    @Test
    public void completeChallenge_whenNotFound_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> challengeService.completeChallenge(UUID.randomUUID()));
    }


    @Test
    public void getUserActiveChallenges_shouldReturnOnlyActiveForUser() {
        // Given
        ChallengeRequest request = AnalyticsFactory.getChallengeRequest(userId);
        challengeService.startChallenge(request);

        // When
        List<ChallengeDto> activeList = challengeService.getUserActiveChallenges(userId);

        // Then
        assertEquals(1, activeList.size());
        assertEquals(userId, activeList.get(0).getUserId());
    }

    @Test
    public void getAllActiveChallenges_shouldReturnActiveChallengesForAllUsers() {
        // Given
        UUID secondUserId = UUID.randomUUID();
        challengeService.startChallenge(AnalyticsFactory.getChallengeRequest(userId));
        challengeService.startChallenge(AnalyticsFactory.getChallengeRequest(secondUserId));

        // When
        List<ChallengeDto> allActive = challengeService.getAllActiveChallenges();

        // Then
        assertEquals(2, allActive.size());
    }

    @Test
    public void updateProgress_forWorkoutChallenge_shouldCalculateCorrectPercent() {
        // Given (3 workout streak days -> 3 * 20 = 60%)
        challengeService.startChallenge(AnalyticsFactory.getChallengeRequest(userId));
        AchievementCheckRequest progressReq = AnalyticsFactory.getWorkoutProgressRequest(userId, 3);

        // When
        challengeService.updateProgressForUser(userId, progressReq);

        // Then
        var activeChallenges = challengeRepository.findByUserIdAndStatus(userId, ChallengeStatus.ACTIVE);
        assertEquals(1, activeChallenges.size());
        assertEquals(60, activeChallenges.get(0).getProgressPercent());
    }

    @Test
    public void updateProgress_forWaterChallenge_shouldCalculateByWaterIntake() {
        // Given (1500 ml water intake -> (1500/3000)*100 = 50%)
        ChallengeRequest request = ChallengeRequest.builder()
                .userId(userId)
                .challengeType(ChallengeType.DRINK_3L_WATER)
                .build();
        challengeService.startChallenge(request);

        AchievementCheckRequest progressReq = AnalyticsFactory.getWaterProgressRequest(userId, 1500);

        // When
        challengeService.updateProgressForUser(userId, progressReq);

        // Then
        var activeChallenges = challengeRepository.findByUserIdAndStatus(userId, ChallengeStatus.ACTIVE);
        assertEquals(50, activeChallenges.get(0).getProgressPercent());
    }

    @Test
    public void updateProgress_forCalorieGoalChallenge_shouldCalculateByConsecutiveDays() {
        // Given (5 consecutive days -> 5 * 14 = 70%)
        ChallengeRequest request = ChallengeRequest.builder()
                .userId(userId)
                .challengeType(ChallengeType.HIT_CALORIE_GOAL_7_DAYS)
                .build();
        challengeService.startChallenge(request);

        AchievementCheckRequest progressReq = AnalyticsFactory.getConsecutiveDaysProgressRequest(userId, 5);

        // When
        challengeService.updateProgressForUser(userId, progressReq);

        // Then
        var activeChallenges = challengeRepository.findByUserIdAndStatus(userId, ChallengeStatus.ACTIVE);
        assertEquals(70, activeChallenges.get(0).getProgressPercent());
    }

    @Test
    public void updateProgress_forBurnCaloriesChallenge_shouldCalculateByBurnedCalories() {
        // Given (250 calories burned -> (250/500)*100 = 50%)
        ChallengeRequest request = ChallengeRequest.builder()
                .userId(userId)
                .challengeType(ChallengeType.BURN_500_DAILY)
                .build();
        challengeService.startChallenge(request);

        AchievementCheckRequest progressReq = AnalyticsFactory.getBurnedCaloriesProgressRequest(userId, 250);

        // When
        challengeService.updateProgressForUser(userId, progressReq);

        // Then
        var activeChallenges = challengeRepository.findByUserIdAndStatus(userId, ChallengeStatus.ACTIVE);
        assertEquals(50, activeChallenges.get(0).getProgressPercent());
    }

    @Test
    public void deleteChallenge_shouldRemoveFromDatabase() {
        // Given
        ChallengeDto started = challengeService.startChallenge(AnalyticsFactory.getChallengeRequest(userId));

        // When
        challengeService.deleteChallenge(started.getId());

        // Then
        assertFalse(challengeRepository.existsById(started.getId()));
    }
}