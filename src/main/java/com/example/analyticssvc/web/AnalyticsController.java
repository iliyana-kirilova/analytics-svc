package com.example.analyticssvc.web;

import com.example.analyticssvc.service.AchievementService;
import com.example.analyticssvc.service.ChallengeService;
import com.example.analyticssvc.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AchievementService achievementService;
    private final ChallengeService challengeService;

    public AnalyticsController(AchievementService achievementService,
                               ChallengeService challengeService) {
        this.achievementService = achievementService;
        this.challengeService = challengeService;
    }

    // Achievements
    @PostMapping("/achievements/check")
    public ResponseEntity<List<AchievementDto>> checkAchievements(
            @RequestBody AchievementCheckRequest request) {
        return ResponseEntity.ok(achievementService.checkAndSave(request));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDto>> getUserAchievements(
            @RequestParam UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

    @GetMapping("/achievements/all")
    public ResponseEntity<List<AchievementDto>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @DeleteMapping("/achievements/{id}")
    public ResponseEntity<Void> archiveAchievement(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        achievementService.archiveAchievement(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/achievements/{id}/admin")
    public ResponseEntity<Void> deleteAchievement(@PathVariable UUID id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<WeeklySummaryDto> getWeeklySummary(
            @RequestParam UUID userId) {
        return ResponseEntity.ok(achievementService.getWeeklySummary(userId));
    }

    // Challenges
    @PostMapping("/challenges")
    public ResponseEntity<ChallengeDto> startChallenge(
            @RequestBody ChallengeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(challengeService.startChallenge(request));
    }

    @DeleteMapping("/challenges/{id}")
    public ResponseEntity<Void> abandonChallenge(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        challengeService.abandonChallenge(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/challenges/{id}/complete")
    public ResponseEntity<Void> completeChallenge(@PathVariable UUID id) {
        challengeService.completeChallenge(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeDto>> getUserChallenges(
            @RequestParam UUID userId) {
        return ResponseEntity.ok(
                challengeService.getUserActiveChallenges(userId));
    }

    @GetMapping("/challenges/all")
    public ResponseEntity<List<ChallengeDto>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllActiveChallenges());
    }

    @PostMapping("/challenges/progress")
    public ResponseEntity<Void> updateChallengeProgress(
            @RequestBody AchievementCheckRequest request) {
        challengeService.updateProgressForUser(request.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/weekly-summary/record")
    public ResponseEntity<Void> recordDailySnapshot(
            @RequestBody DailySnapshotRequest request) {
        achievementService.recordSnapshot(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/challenges/{id}/admin")
    public ResponseEntity<Void> deleteChallenge(@PathVariable UUID id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}
