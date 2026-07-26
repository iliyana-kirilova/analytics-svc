package com.example.analyticssvc.service;

import com.example.analyticssvc.model.challenge.ChallengeStatus;
import com.example.analyticssvc.model.challenge.UserChallenge;
import com.example.analyticssvc.repository.UserChallengeRepository;
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
}
