package com.example.analyticssvc.repository;

import com.example.analyticssvc.model.challenge.ChallengeStatus;
import com.example.analyticssvc.model.challenge.ChallengeType;
import com.example.analyticssvc.model.challenge.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, UUID> {
    List<UserChallenge> findByUserIdAndStatus(
            UUID userId, ChallengeStatus status);

    List<UserChallenge> findAllByStatus(ChallengeStatus status);

    boolean existsByUserIdAndChallengeTypeAndStatus(
            UUID userId, ChallengeType challengeType, ChallengeStatus status);
}
