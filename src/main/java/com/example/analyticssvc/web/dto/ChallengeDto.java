package com.example.analyticssvc.web.dto;

import com.example.analyticssvc.model.challenge.ChallengeStatus;
import com.example.analyticssvc.model.challenge.ChallengeType;
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
public class ChallengeDto {
    private UUID id;
    private UUID userId;
    private ChallengeType challengeType;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;
    private ChallengeStatus status;
    private int progressPercent;
}
