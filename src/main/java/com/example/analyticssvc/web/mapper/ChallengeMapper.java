package com.example.analyticssvc.web.mapper;

import com.example.analyticssvc.model.challenge.UserChallenge;
import com.example.analyticssvc.web.dto.ChallengeDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ChallengeMapper {
    public static ChallengeDto toDto(UserChallenge challenge) {
        if (challenge == null) {
            return null;
        }
        return ChallengeDto.builder()
                .id(challenge.getId())
                .userId(challenge.getUserId())
                .challengeType(challenge.getChallengeType())
                .startedAt(challenge.getStartedAt())
                .deadline(challenge.getDeadline())
                .status(challenge.getStatus())
                .progressPercent(challenge.getProgressPercent())
                .build();
    }

    public static UserChallenge toEntity(ChallengeDto dto) {
        if (dto == null) {
            return null;
        }
        return UserChallenge.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .challengeType(dto.getChallengeType())
                .startedAt(dto.getStartedAt())
                .deadline(dto.getDeadline())
                .status(dto.getStatus())
                .progressPercent(dto.getProgressPercent())
                .build();
    }

}
