package com.example.analyticssvc.web.dto;

import com.example.analyticssvc.model.challenge.ChallengeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRequest {
    private UUID userId;
    private ChallengeType challengeType;
}
