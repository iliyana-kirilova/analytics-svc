# 📊 NutriPulse Analytics Service

A dedicated REST microservice for the NutriPulse fitness tracking application.
Handles achievements, challenges, weekly summaries, and daily data snapshots.
Communicates exclusively with the NutriPulse main application via REST API,
secured with API Key authentication.

---

## 🛠️ Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Java 17                           |
| Framework   | Spring Boot 3.4.0                 |
| Database    | MySQL (`analytics_db`)            |
| Security    | API Key Authentication (stateless)|
| Caching     | Spring Cache (Simple)             |
| Scheduling  | Spring Scheduler (Cron + Trigger) |
| Build Tool  | Maven                             |
| Port        | 8081                              |

---

## 🏗️ Architecture
NutriPulse Main App (port 8080)
│
│ Feign Client (X-API-Key header)
▼
analytics-svc (port 8081)
│
▼
analytics_db (MySQL)

The analytics-svc runs independently. All endpoints require a valid
`X-API-Key` header. The main application sends requests automatically
via Feign Client after every user action (meal, workout, water log).

---

## 🗂️ Domain Entities

| Entity              | Purpose                                      |
|---------------------|----------------------------------------------|
| `UserAchievement`   | Tracks unlocked achievements per user        |
| `UserChallenge`     | Tracks active/completed challenges per user  |
| `DailySnapshot`     | Daily data snapshot for weekly summary       |

---

## ✨ Features

### Achievements
- 15 achievement types automatically checked on every user action
- Examples: `FIRST_MEAL_LOGGED`, `COMPLETE_DAY`, `STREAK_7_DAYS`,
  `BURNED_1000_CALORIES`, `HYDRATION_MASTER`
- Once unlocked, achievements are never re-unlocked for the same user
- Users can archive (soft-delete) achievements they don't want to see
- Admin can permanently delete any achievement

### Challenges
- 5 challenge types users can start: `WORKOUT_5_TIMES`, `DRINK_3L_WATER`,
  `HIT_CALORIE_GOAL_7_DAYS`, `LOG_EVERY_DAY_MONTH`, `BURN_500_DAILY`
- Progress tracked automatically when user logs activities
- Users can abandon active challenges
- Admin can complete or delete any challenge
- Overdue challenges automatically expired by scheduler

### Weekly Summary
- Average daily calories consumed, water intake, and calories burned
- Total achievements unlocked in the last 7 days
- Powered by `DailySnapshot` records sent from the main app

---

## ✅ Valid Domain Functionalities

| # | Functionality              | Triggered By    | Endpoint                          | Entity Changed    |
|---|----------------------------|-----------------|-----------------------------------|-------------------|
| 1 | Start a challenge          | User form submit | `POST /challenges`               | `UserChallenge`   |
| 2 | Abandon a challenge        | User button click| `DELETE /challenges/{id}`        | `UserChallenge`   |
| 3 | Archive an achievement     | User button click| `DELETE /achievements/{id}`      | `UserAchievement` |
| 4 | Check and unlock achievements | Auto on action | `POST /achievements/check`      | `UserAchievement` |

---

## 🌐 REST API Endpoints

### Achievements

| Method | Path                              | Description                        |
|--------|-----------------------------------|------------------------------------|
| POST   | `/api/v1/analytics/achievements/check` | Check and unlock achievements |
| GET    | `/api/v1/analytics/achievements`  | Get user's active achievements     |
| GET    | `/api/v1/analytics/achievements/all` | Get all achievements (admin)    |
| DELETE | `/api/v1/analytics/achievements/{id}` | Archive achievement (user)    |
| DELETE | `/api/v1/analytics/achievements/{id}/admin` | Delete achievement (admin) |

### Challenges

| Method | Path                                        | Description                    |
|--------|---------------------------------------------|--------------------------------|
| POST   | `/api/v1/analytics/challenges`              | Start a new challenge          |
| GET    | `/api/v1/analytics/challenges`              | Get user's active challenges   |
| GET    | `/api/v1/analytics/challenges/all`          | Get all challenges (admin)     |
| DELETE | `/api/v1/analytics/challenges/{id}`         | Abandon a challenge (user)     |
| DELETE | `/api/v1/analytics/challenges/{id}/admin`   | Delete a challenge (admin)     |
| PUT    | `/api/v1/analytics/challenges/{id}/complete`| Complete a challenge (admin)   |
| POST   | `/api/v1/analytics/challenges/progress`     | Update challenge progress      |

### Weekly Summary

| Method | Path                                  | Description                  |
|--------|---------------------------------------|------------------------------|
| GET    | `/api/v1/analytics/weekly-summary`    | Get weekly summary for user  |
| POST   | `/api/v1/analytics/weekly-summary/record` | Record daily snapshot    |

---

## 🔒 Security

All endpoints are protected with API Key authentication.

Header: X-API-Key: <your-secret-key>

- Missing key → `401 Unauthorized`
- Wrong key → `403 Forbidden`
- Valid key → request proceeds

The filter (`ApiKeyAuthenticationFilter`) runs before every request.
Session policy is `STATELESS` — no session is created.

---

## 🛡️ Error Handling

Global exception handler (`GlobalExceptionHandler`) returns structured JSON:

```json
{
  "timestamp": "2026-08-11T10:30:00",
  "status": 404,
  "message": "Challenge not found: ...",
  "path": "/api/v1/analytics/challenges/..."
}
```

| Handler                            | Type            | HTTP Code |
|------------------------------------|-----------------|-----------|
| `AnalyticsException`               | Custom base     | varies    |
| `ChallengeNotFoundException`       | Custom          | 404       |
| `AchievementNotFoundException`     | Custom          | 404       |
| `MethodArgumentTypeMismatchException` | Built-in Spring | 400    |
| `Exception`                        | Fallback        | 500       |

---

## ⏱️ Scheduling

| Job   | Type    | Schedule        | Effect                                        |
|-------|---------|-----------------|-----------------------------------------------|
| Job 1 | Cron    | Every day 01:00 | Expires overdue active challenges             |
| Job 2 | Trigger | Every 12 hours  | Logs total achievements and challenges count  |

---

## 💾 Caching

| Cache              | Method                  | Evicted On         |
|--------------------|-------------------------|--------------------|
| `userAchievements` | `getUserAchievements()` | `checkAndSave()`   |

---

## 🧪 Testing

| Type        | Class                          | What It Tests              |
|-------------|--------------------------------|----------------------------|
| Unit        | `AchievementCheckerUnitTest`   | Achievement unlock logic   |
| Integration | `ChallengeServiceItTest`       | Challenge lifecycle (H2)   |
| API         | `AnalyticsControllerApiTest`   | Endpoints + API Key auth   |

Minimum line coverage: **70%**

---

## 🗂️ Project Structure

src/
├── main/java/com/example/analyticssvc/
│ ├── config/ # SecurityConfig, ApiKeyAuthenticationFilter,
│ │ # ApiKeyAuthentication, GlobalExceptionHandler
│ ├── exception/ # AnalyticsException + custom exceptions
│ ├── model/
│ │ ├── achievement/ # UserAchievement, AchievementType, AchievementStatus
│ │ ├── challenge/ # UserChallenge, ChallengeType, ChallengeStatus
│ │ └── DailySnapshot.java
│ ├── repository/ # JPA repositories
│ ├── scheduler/ # AnalyticsScheduler
│ ├── service/ # AchievementService, ChallengeService,
│ │ # AchievementChecker
│ └── web/
│ ├── dto/ # Request and response DTOs
│ ├── mapper/ # Entity ↔ DTO mappers
│ └── AnalyticsController.java
└── resources/
└── application.properties

---

## 🚀 Running the Service

### Prerequisites
- Java 17+
- MySQL running locally

### Steps
1. Clone the repository
2. Create the database:
```sql
   CREATE DATABASE analytics_db;
```
3. Configure `src/main/resources/application.properties`:
```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/analytics_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   analytics.service.api-key=your-secret-key
```
4. Run:
   mvn spring-boot:run

5. Service starts on `http://localhost:8081`

### Running Both Applications
Start analytics-svc first, then the main NutriPulse application.
The main app will gracefully degrade if analytics-svc is unavailable.