package com.example.analyticssvc.service;

import com.example.analyticssvc.model.achievment.AchievementType;
import com.example.analyticssvc.model.achievment.UserAchievement;
import com.example.analyticssvc.repository.UserAchievementRepository;
import com.example.analyticssvc.util.AnalyticsFactory;
import com.example.analyticssvc.web.dto.AchievementCheckRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementCheckerUnitTest {

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @InjectMocks
    private AchievementChecker achievementChecker;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    public void check_whenFirstMealAdded_andNotAlreadyUnlocked_shouldUnlockAchievement() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getMealAddedRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.FIRST_MEAL_LOGGED))
                .thenReturn(false);

        when(userAchievementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().anyMatch(a ->
                a.getAchievementType() == AchievementType.FIRST_MEAL_LOGGED));
        verify(userAchievementRepository).save(any());
    }

    @Test
    public void check_whenFirstMealAdded_andAlreadyUnlocked_shouldNotUnlockAgain() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getMealAddedRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.FIRST_MEAL_LOGGED))
                .thenReturn(true);

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().noneMatch(a ->
                a.getAchievementType() == AchievementType.FIRST_MEAL_LOGGED));
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    public void check_whenWaterGoalMet_shouldUnlockDailyWaterGoalMet() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getWaterGoalRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.DAILY_WATER_GOAL_MET))
                .thenReturn(false);
        when(userAchievementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().anyMatch(a ->
                a.getAchievementType() == AchievementType.DAILY_WATER_GOAL_MET));
    }

    @Test
    public void check_whenCalorieGoalMet_shouldUnlockCalorieGoalMet() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getCalorieGoalRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.CALORIE_GOAL_MET))
                .thenReturn(false);
        when(userAchievementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().anyMatch(a ->
                a.getAchievementType() == AchievementType.CALORIE_GOAL_MET));
    }

    @Test
    public void check_whenBurned1000Calories_shouldUnlockBurned1000Calories() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getBurnedCaloriesRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.BURNED_1000_CALORIES))
                .thenReturn(false);
        when(userAchievementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().anyMatch(a ->
                a.getAchievementType() == AchievementType.BURNED_1000_CALORIES));
    }

    @Test
    public void check_whenCompleteDayFlag_shouldUnlockCompleteDayAchievement() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getCompleteDayRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.COMPLETE_DAY))
                .thenReturn(false);
        when(userAchievementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().anyMatch(a ->
                a.getAchievementType() == AchievementType.COMPLETE_DAY));
    }

    @Test
    public void check_whenMacroBalanceMet_shouldUnlockMacroBalanceMaster() {
        // Given
        AchievementCheckRequest request = AnalyticsFactory.getMacroBalanceRequest(userId);

        when(userAchievementRepository
                .existsByUserIdAndAchievementType(userId, AchievementType.MACRO_BALANCE_MASTER))
                .thenReturn(false);
        when(userAchievementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        List<UserAchievement> result = achievementChecker.check(request);

        // Then
        assertTrue(result.stream().anyMatch(a ->
                a.getAchievementType() == AchievementType.MACRO_BALANCE_MASTER));
        verify(userAchievementRepository).save(any());
    }
}