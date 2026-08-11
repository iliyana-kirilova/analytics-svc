# NutriPulse Analytics Service

A REST microservice for the NutriPulse fitness tracking application.
Handles achievements, challenges, and weekly summaries.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4 |
| Database | MySQL (analytics_db) |
| Security | API Key Authentication |
| Communication | REST — consumed via Feign Client |

## Architecture

The analytics-svc runs independently on port 8081 and communicates
with the main NutriPulse application via REST API.
All requests require a valid X-API-Key header.

## Endpoints

| Method | Path | Description |
|---|---|---|
| POST | /api/v1/analytics/achievements/check | Check and unlock achievements |
| GET | /api/v1/analytics/achievements | Get user achievements |
| DELETE | /api/v1/analytics/achievements/{id} | Archive achievement |
| GET | /api/v1/analytics/weekly-summary | Get weekly summary |
| POST | /api/v1/analytics/challenges | Start a challenge |
| DELETE | /api/v1/analytics/challenges/{id} | Abandon a challenge |
| PUT | /api/v1/analytics/challenges/{id}/complete | Complete a challenge |
| GET | /api/v1/analytics/challenges | Get user challenges |
| POST | /api/v1/analytics/challenges/progress | Update challenge progress |
| POST | /api/v1/analytics/weekly-summary/record | Record daily snapshot |

## Running

1. Create MySQL database: `CREATE DATABASE analytics_db;`
2. Set environment variable: `API_KEY=your-secret-key`
3. Run: `mvn spring-boot:run`
4. Service starts on `http://localhost:8081`

## Security

All endpoints are protected with API Key authentication.
Include the header `X-API-Key: your-secret-key` in every request.

## Domain Functionalities

1. **Start a Challenge** — user selects a challenge type,
   main app sends POST to analytics-svc, UserChallenge entity is created,
   challenge appears in user's analytics page.

2. **Archive an Achievement** — user clicks ✕ on an unlocked achievement,
   main app sends DELETE to analytics-svc,
   UserAchievement status changes to ARCHIVED,
   achievement disappears from the unlocked list.