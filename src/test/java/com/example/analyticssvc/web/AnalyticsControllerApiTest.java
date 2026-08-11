package com.example.analyticssvc.web;

import com.example.analyticssvc.config.ApiKeyAuthenticationFilter;
import com.example.analyticssvc.config.SecurityConfig;
import com.example.analyticssvc.service.AchievementService;
import com.example.analyticssvc.service.ChallengeService;
import com.example.analyticssvc.web.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

// Static imports за Mockito и MockMvc
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AnalyticsController.class)
@Import({SecurityConfig.class, ApiKeyAuthenticationFilter.class})
@TestPropertySource(properties = "analytics.service.api-key=test-api-key")
public class AnalyticsControllerApiTest {

    @MockitoBean
    private AchievementService achievementService;

    @MockitoBean
    private ChallengeService challengeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_KEY = "test-api-key";

    @Test
    public void getUserChallenges_withValidApiKey_shouldReturn200AndList()
            throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        List<ChallengeDto> challenges = List.of(new ChallengeDto(), new ChallengeDto());
        when(challengeService.getUserActiveChallenges(any())).thenReturn(challenges);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", API_KEY);

        // When & Then
        mockMvc.perform(get("/api/v1/analytics/challenges")
                        .param("userId", userId.toString())
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getUserChallenges_withoutApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/challenges")
                        .param("userId", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserChallenges_withWrongApiKey_shouldReturn403() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", "wrong-key");

        mockMvc.perform(get("/api/v1/analytics/challenges")
                        .param("userId", UUID.randomUUID().toString())
                        .headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserAchievements_withValidApiKey_shouldReturn200() throws Exception {
        when(achievementService.getUserAchievements(any())).thenReturn(List.of());

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", API_KEY);

        mockMvc.perform(get("/api/v1/analytics/achievements")
                        .param("userId", UUID.randomUUID().toString())
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    public void checkAchievements_shouldReturn200AndList() throws Exception {
        AchievementCheckRequest request = new AchievementCheckRequest();
        when(achievementService.checkAndSave(any())).thenReturn(List.of(new AchievementDto()));

        mockMvc.perform(post("/api/v1/analytics/achievements/check")
                        .header("X-API-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllAchievements_shouldReturn200AndList() throws Exception {
        when(achievementService.getAllAchievements()).thenReturn(List.of(new AchievementDto()));

        mockMvc.perform(get("/api/v1/analytics/achievements/all")
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void archiveAchievement_shouldReturn204NoContent() throws Exception {
        UUID achievementId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/analytics/achievements/{id}", achievementId)
                        .param("userId", userId.toString())
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isNoContent());

        verify(achievementService).archiveAchievement(achievementId, userId);
    }

    @Test
    public void deleteAchievement_shouldReturn204NoContent() throws Exception {
        UUID achievementId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/analytics/achievements/{id}/admin", achievementId)
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isNoContent());

        verify(achievementService).deleteAchievement(achievementId);
    }

    @Test
    public void getWeeklySummary_shouldReturn200AndSummary() throws Exception {
        UUID userId = UUID.randomUUID();
        when(achievementService.getWeeklySummary(any())).thenReturn(new WeeklySummaryDto());

        mockMvc.perform(get("/api/v1/analytics/weekly-summary")
                        .param("userId", userId.toString())
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isOk());
    }

    @Test
    public void recordDailySnapshot_shouldReturn200() throws Exception {
        DailySnapshotRequest request = new DailySnapshotRequest();

        mockMvc.perform(post("/api/v1/analytics/weekly-summary/record")
                        .header("X-API-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(achievementService).recordSnapshot(any(DailySnapshotRequest.class));
    }

    // ==========================================
    // CHALLENGES ENDPOINTS
    // ==========================================

    @Test
    public void startChallenge_shouldReturn201Created() throws Exception {
        ChallengeRequest request = new ChallengeRequest();
        when(challengeService.startChallenge(any())).thenReturn(new ChallengeDto());

        mockMvc.perform(post("/api/v1/analytics/challenges")
                        .header("X-API-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void abandonChallenge_shouldReturn204NoContent() throws Exception {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/analytics/challenges/{id}", challengeId)
                        .param("userId", userId.toString())
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isNoContent());

        verify(challengeService).abandonChallenge(challengeId, userId);
    }

    @Test
    public void completeChallenge_shouldReturn200Ok() throws Exception {
        UUID challengeId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/analytics/challenges/{id}/complete", challengeId)
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isOk());

        verify(challengeService).completeChallenge(challengeId);
    }

    @Test
    public void getAllChallenges_shouldReturn200AndList() throws Exception {
        when(challengeService.getAllActiveChallenges()).thenReturn(List.of(new ChallengeDto()));

        mockMvc.perform(get("/api/v1/analytics/challenges/all")
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void updateChallengeProgress_shouldReturn200Ok() throws Exception {
        AchievementCheckRequest request = new AchievementCheckRequest();

        mockMvc.perform(post("/api/v1/analytics/challenges/progress")
                        .header("X-API-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(challengeService).updateProgressForUser(any(), any(AchievementCheckRequest.class));
    }

    @Test
    public void deleteChallengeAdmin_shouldReturn204NoContent() throws Exception {
        UUID challengeId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/analytics/challenges/{id}/admin", challengeId)
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isNoContent());

        verify(challengeService).deleteChallenge(challengeId);
    }
}
