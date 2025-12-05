# RoutineRevo Server

Backend server for the RoutineRevo fitness application built with Spring Boot.

## Quick Start

### Prerequisites

- Java 17 or higher
- PostgreSQL 14 or higher
- Maven 3.6+ (or use the included `./mvnw` wrapper)
- Docker & Docker Compose (optional, for containerized database)

### Environment Setup

1. **Clone the repository** (if not already done)

2. **Set up environment variables**
   
   Create a `local.env` file in the project root:
   
   ```bash
   # JWT Secrets
   JWT_SECRET=your-secret-key-here-min-256-bits
   JWT_PASSWORD_RESET_SECRET=your-password-reset-secret-here
   JWT_ONBOARDING_SECRET=your-onboarding-secret-here
   
   # AWS SES Configuration
   AWS_SDK_ACCESS_KEY=your-aws-access-key
   AWS_SDK_SECRET_KEY=your-aws-secret-key
   
   # Frontend URL
   FRONTEND_URL=http://localhost:5173
   ```

3. **Start PostgreSQL Database**
   
   Using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   
   Or manually create the database:
   ```bash
   psql -U postgres
   CREATE DATABASE routine_revo_db;
   ```

4. **Run the application**
   
   **Option A: Using the startup script (recommended for local dev)**
   ```bash
   ./start-local.sh
   ```
   
   **Option B: Using Maven**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   **Option C: Using IntelliJ IDEA**
   - Open the project in IntelliJ
   - Set up environment variables in Run Configuration
   - Set Active Profile to `local`
   - Run `RoutineRevoServerApplication`

## Development Features

### Local Data Seeding

When running with the `local` profile (default), the application automatically seeds the database with test users. See [LOCAL_SEEDING.md](LOCAL_SEEDING.md) for details.

**Test Users:**
- **Admin**: `admin@routinerevo.com` / `admin123`
- **Coach**: `john.coach@routinerevo.com` / `coach123`
- **Coach**: `sarah.coach@routinerevo.com` / `coach123`
- **Member**: `alice.member@routinerevo.com` / `member123`
- And more... (see LOCAL_SEEDING.md)

### Profiles

The application supports multiple profiles:

- **`local`** (default): Development with data seeding, verbose logging
- **`prod`**: Production with minimal logging, no seeding

Switch profiles using:
```bash
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

## Project Structure

```
src/
├── main/
│   ├── java/com/lucashthiele/routine_revo_server/
│   │   ├── config/              # Application configuration
│   │   ├── domain/              # Domain models
│   │   │   ├── user/
│   │   │   └── shared/
│   │   ├── gateway/             # Gateway interfaces
│   │   ├── infrastructure/      # Infrastructure implementations
│   │   │   ├── data/           # Database entities & repositories
│   │   │   │   ├── user/
│   │   │   │   └── seeder/     # Data seeders
│   │   │   ├── notification/   # Email services
│   │   │   ├── security/       # Security implementations
│   │   │   └── web/            # REST controllers
│   │   └── usecase/            # Use case implementations
│   │       ├── auth/
│   │       ├── onboarding/
│   │       ├── passwordreset/
│   │       └── user/
│   └── resources/
│       ├── application.properties          # Main config
│       ├── application-local.properties    # Local dev config
│       ├── application-prod.properties     # Production config
│       ├── db/migration/                   # Flyway migrations
│       └── templates/email/                # Email templates
└── test/
```

## Database Migrations

This project uses Flyway for database version control.

### Current Migrations

- `V1__create_users_table.sql` - Initial user table
- `V2__remove_constraint_hashed_password.sql` - Remove password constraint
- `V3__create_coach_id_users.sql` - Add coach relationships
- `V4__seed_initial_data.sql` - Seed test data (local only)

### Creating New Migrations

1. Create a new file in `src/main/resources/db/migration/`
2. Name it following the pattern: `V{version}__{description}.sql`
3. Example: `V5__add_workouts_table.sql`

Flyway will automatically run new migrations on application startup.

## API Documentation

The server runs on `http://localhost:42069` by default.

Base URL: `/api/v1`

---

### Error Response Format

All error responses follow this structure:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Error Type",
  "message": "Detailed error message",
  "path": "/api/v1/endpoint"
}
```

#### Common Error Codes

| Status Code | Error Type | Description |
|-------------|------------|-------------|
| 400 | Bad Request | Validation errors, invalid input |
| 401 | Unauthorized | Authentication required or failed |
| 403 | Forbidden | Access denied - insufficient permissions |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Unexpected server error |

---

### Enums Reference

#### Role
```
ADMIN | COACH | MEMBER
```

#### Status
```
PENDING | ACTIVE | INACTIVE
```

#### MuscleGroup
```
CHEST | BACK | SHOULDERS | BICEPS | TRICEPS | LEGS | GLUTES | ABS | CARDIO | FULL_BODY
```

---

## Authentication Endpoints

### POST `/api/v1/auth/login`
Authenticate user and obtain tokens.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| email | string | Yes | Valid email format |
| password | string | Yes | Not blank |

**Response (200 OK):**
```json
{
  "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "COACH"
  }
}
```

**Possible Errors:**
- `400` - Validation failed (invalid email format, blank fields)
- `401` - Invalid credentials

---

### POST `/api/v1/auth/refresh`
Refresh authentication tokens.

**Headers:**
```
X-Refresh-Token: <refresh_token>
```

**Response (200 OK):**
```json
{
  "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "COACH"
  }
}
```

**Possible Errors:**
- `401` - Invalid or expired refresh token

---

## User Endpoints

### POST `/api/v1/users`
Create a new user (Admin only).

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "role": "MEMBER",
  "coachId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | Not blank |
| email | string | Yes | Valid email format |
| role | enum | Yes | ADMIN, COACH, or MEMBER |
| coachId | UUID | No | Required if role is MEMBER |

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Validation failed, duplicate email, invalid coach ID
- `401` - Authentication required
- `403` - Access denied

---

### GET `/api/v1/users`
List users with pagination and filtering.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| name | string | No | - | Filter by name (partial match) |
| role | enum | No | - | Filter by role |
| status | enum | No | - | Filter by status |
| page | integer | No | 0 | Page number (0-indexed) |
| size | integer | No | 10 | Page size |

**Response (200 OK):**
```json
{
  "users": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "John Doe",
      "email": "john@example.com",
      "role": "MEMBER",
      "status": "ACTIVE",
      "coachId": "550e8400-e29b-41d4-a716-446655440001",
      "workoutPerWeek": 3,
      "adherenceRate": 85.5,
      "lastWorkoutDate": "2025-01-15T10:30:00Z",
      "assignedRoutines": [
        {
          "id": "550e8400-e29b-41d4-a716-446655440100",
          "name": "Push Day Routine",
          "expirationDate": "2025-06-01T00:00:00Z",
          "isExpired": false
        }
      ]
    }
  ],
  "total": 100,
  "page": 0,
  "size": 10,
  "totalPages": 10
}
```

> **Note**: `adherenceRate`, `lastWorkoutDate`, and `assignedRoutines` are only populated for users with role `MEMBER`. For other roles, these fields will be `null`.

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied

---

### GET `/api/v1/users/{id}`
Get user by ID.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | User ID |

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "MEMBER",
  "status": "ACTIVE",
  "coachId": "550e8400-e29b-41d4-a716-446655440001",
  "workoutPerWeek": 3,
  "adherenceRate": 85.5,
  "lastWorkoutDate": "2025-01-15T10:30:00Z",
  "assignedRoutines": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440100",
      "name": "Push Day Routine",
      "expirationDate": "2025-06-01T00:00:00Z",
      "isExpired": false
    }
  ]
}
```

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied
- `404` - User not found

---

### PUT `/api/v1/users/{id}`
Update user information.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | User ID |

**Request Body:**
```json
{
  "name": "John Doe Updated",
  "email": "john.updated@example.com"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | Not blank |
| email | string | Yes | Valid email format |

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe Updated",
  "email": "john.updated@example.com",
  "role": "MEMBER",
  "status": "ACTIVE",
  "coachId": "550e8400-e29b-41d4-a716-446655440001",
  "workoutPerWeek": 3,
  "adherenceRate": 85.5,
  "lastWorkoutDate": "2025-01-15T10:30:00Z",
  "assignedRoutines": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440100",
      "name": "Push Day Routine",
      "expirationDate": "2025-06-01T00:00:00Z",
      "isExpired": false
    }
  ]
}
```

**Possible Errors:**
- `400` - Validation failed, duplicate email
- `401` - Authentication required
- `403` - Access denied
- `404` - User not found

---

### DELETE `/api/v1/users/{id}`
Inactivate a user (soft delete).

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | User ID |

**Response (204 No Content):** Empty body

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied
- `404` - User not found

---

### PATCH `/api/v1/users/{userId}/coach`
Link a coach to a member.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| userId | UUID | Yes | Member's user ID |

**Request Body:**
```json
{
  "coachId": "550e8400-e29b-41d4-a716-446655440001"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| coachId | UUID | Yes | Not null, must be a COACH role user |

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Invalid coach (user is not a COACH)
- `401` - Authentication required
- `403` - Access denied
- `404` - User or coach not found

---

### GET `/api/v1/users/members`
Search active members.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| name | string | No | - | Filter by name (partial match) |
| page | integer | No | 0 | Page number (0-indexed) |
| size | integer | No | 10 | Page size |

**Response (200 OK):**
```json
{
  "users": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Alice Member",
      "email": "alice@example.com",
      "role": "MEMBER",
      "status": "ACTIVE",
      "coachId": "550e8400-e29b-41d4-a716-446655440001",
      "workoutPerWeek": 4,
      "adherenceRate": 92.0,
      "lastWorkoutDate": "2025-01-14T16:45:00Z",
      "assignedRoutines": [
        {
          "id": "550e8400-e29b-41d4-a716-446655440100",
          "name": "Full Body Routine",
          "expirationDate": "2025-06-01T00:00:00Z",
          "isExpired": false
        }
      ]
    }
  ],
  "total": 50,
  "page": 0,
  "size": 10,
  "totalPages": 5
}
```

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied

---

### POST `/api/v1/users/members/{memberId}/routines/bulk`
Bulk assign multiple routines to a member.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| memberId | UUID | Yes | Member's user ID |

**Request Body:**
```json
{
  "routineIds": [
    "550e8400-e29b-41d4-a716-446655440100",
    "550e8400-e29b-41d4-a716-446655440101",
    "550e8400-e29b-41d4-a716-446655440102"
  ],
  "expirationDate": "2025-06-01T00:00:00Z"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| routineIds | UUID[] | Yes | At least one routine ID required |
| expirationDate | ISO timestamp | No | Applied to all routines if provided |

**Response (200 OK):**
```json
{
  "assignedCount": 3,
  "message": "3 routine(s) assigned successfully to member"
}
```

**Possible Errors:**
- `400` - Validation failed (empty routine list)
- `401` - Authentication required
- `403` - Access denied
- `404` - Member or routine not found

---

## Profile Endpoints

### GET `/api/v1/me`
Get authenticated user's profile.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "MEMBER",
  "status": "ACTIVE",
  "coachId": "550e8400-e29b-41d4-a716-446655440001",
  "workoutPerWeek": 3,
  "adherenceRate": 85.5
}
```

**Possible Errors:**
- `401` - Authentication required

---

### PUT `/api/v1/me`
Update authenticated user's profile.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Request Body:**
```json
{
  "name": "John Doe Updated"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | 2-100 characters |

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe Updated",
  "email": "john@example.com",
  "role": "MEMBER",
  "status": "ACTIVE",
  "coachId": "550e8400-e29b-41d4-a716-446655440001",
  "workoutPerWeek": 3,
  "adherenceRate": 85.5
}
```

**Possible Errors:**
- `400` - Validation failed
- `401` - Authentication required

---

## Exercise Endpoints

### POST `/api/v1/exercises`
Create a new exercise (multipart/form-data).

**Headers:**
```
Authorization: Bearer <auth_token>
Content-Type: multipart/form-data
```

**Form Data:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| data | JSON string | Yes | Exercise data (see below) |
| file | File | No | Exercise image (JPEG, PNG, GIF, WebP) |

**data JSON:**
```json
{
  "name": "Bench Press",
  "muscleGroup": "CHEST",
  "description": "A compound chest exercise",
  "equipment": "Barbell, Bench"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | Not blank |
| muscleGroup | enum | Yes | Valid MuscleGroup |
| description | string | No | - |
| equipment | string | No | - |

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Exercise created successfully"
}
```

**Possible Errors:**
- `400` - Validation failed (missing name or muscle group)
- `401` - Authentication required
- `403` - Access denied

---

### GET `/api/v1/exercises`
Search exercises with pagination and filtering.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| name | string | No | - | Filter by name (partial match) |
| muscleGroup | enum | No | - | Filter by muscle group |
| page | integer | No | 0 | Page number (0-indexed) |
| size | integer | No | 10 | Page size |

**Response (200 OK):**
```json
{
  "exercises": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Bench Press",
      "muscleGroup": "CHEST",
      "description": "A compound chest exercise",
      "equipment": "Barbell, Bench",
      "imageUrl": "https://storage.example.com/exercises/bench-press.jpg",
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    }
  ],
  "total": 100,
  "page": 0,
  "size": 10,
  "totalPages": 10
}
```

**Possible Errors:**
- `401` - Authentication required

---

### GET `/api/v1/exercises/{id}`
Get exercise by ID.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Exercise ID |

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Bench Press",
  "muscleGroup": "CHEST",
  "description": "A compound chest exercise",
  "equipment": "Barbell, Bench",
  "imageUrl": "https://storage.example.com/exercises/bench-press.jpg",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

**Possible Errors:**
- `401` - Authentication required
- `404` - Exercise not found

---

### PUT `/api/v1/exercises/{id}`
Update an exercise (multipart/form-data).

**Headers:**
```
Authorization: Bearer <auth_token>
Content-Type: multipart/form-data
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Exercise ID |

**Form Data:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| data | JSON string | Yes | Exercise data (see below) |
| file | File | No | New exercise image |

**data JSON:**
```json
{
  "name": "Incline Bench Press",
  "muscleGroup": "CHEST",
  "description": "An incline variation",
  "equipment": "Barbell, Incline Bench"
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Incline Bench Press",
  "muscleGroup": "CHEST",
  "description": "An incline variation",
  "equipment": "Barbell, Incline Bench",
  "imageUrl": "https://storage.example.com/exercises/incline-bench.jpg",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T11:00:00"
}
```

**Possible Errors:**
- `400` - Validation failed
- `401` - Authentication required
- `403` - Access denied
- `404` - Exercise not found

---

### DELETE `/api/v1/exercises/{id}`
Delete an exercise.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Exercise ID |

**Response (204 No Content):** Empty body

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied
- `404` - Exercise not found

---

## Routine Endpoints

### GET `/api/v1/routines`
List routines with pagination and filtering.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| creatorId | UUID | No | - | Filter by creator |
| memberId | UUID | No | - | Filter by assigned member |
| isExpired | boolean | No | - | Filter by expiration status |
| templatesOnly | boolean | No | false | Only return routines without assigned members |
| page | integer | No | 0 | Page number (0-indexed) |
| size | integer | No | 20 | Page size |

**Response (200 OK):**
```json
{
  "routines": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440100",
      "name": "Push Day Routine",
      "description": "Chest, shoulders, and triceps workout",
      "expirationDate": "2025-06-01T00:00:00Z",
      "isExpired": false,
      "creatorId": "550e8400-e29b-41d4-a716-446655440001",
      "memberId": "550e8400-e29b-41d4-a716-446655440002",
      "itemCount": 8,
      "items": [
        {
          "id": "550e8400-e29b-41d4-a716-446655440200",
          "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
          "exerciseName": "Bench Press",
          "exerciseImageUrl": "https://storage.example.com/exercises/bench.jpg",
          "sets": 4,
          "reps": "8-10",
          "load": 60.0,
          "restTime": "90s",
          "sequenceOrder": 1
        }
      ],
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    }
  ],
  "total": 50,
  "page": 0,
  "size": 20,
  "totalPages": 3
}
```

**Possible Errors:**
- `401` - Authentication required

---

### POST `/api/v1/routines`
Create a new routine.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Request Body:**
```json
{
  "name": "Push Day Routine",
  "description": "Chest, shoulders, and triceps workout",
  "expirationDate": "2025-06-01T00:00:00Z",
  "creatorId": "550e8400-e29b-41d4-a716-446655440001",
  "memberId": "550e8400-e29b-41d4-a716-446655440002",
  "items": [
    {
      "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
      "sets": 4,
      "reps": "8-10",
      "load": 60.0,
      "restTime": "90s",
      "sequenceOrder": 1
    }
  ]
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | Not blank |
| description | string | No | - |
| expirationDate | ISO timestamp | No | - |
| creatorId | UUID | Yes | Not null |
| memberId | UUID | No | - |
| items | array | No | Valid items array |

**Routine Item:**
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| exerciseId | UUID | Yes | Not null |
| sets | integer | Yes | Not null |
| reps | string | Yes | Not null |
| load | double | No | - |
| restTime | string | No | - |
| sequenceOrder | integer | Yes | Not null |

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440100",
  "message": "Routine created successfully"
}
```

**Possible Errors:**
- `400` - Validation failed
- `401` - Authentication required
- `403` - Access denied
- `404` - Exercise not found

---

### GET `/api/v1/routines/{id}`
Get routine by ID.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Routine ID |

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440100",
  "name": "Push Day Routine",
  "description": "Chest, shoulders, and triceps workout",
  "expirationDate": "2025-06-01T00:00:00Z",
  "isExpired": false,
  "creatorId": "550e8400-e29b-41d4-a716-446655440001",
  "memberId": "550e8400-e29b-41d4-a716-446655440002",
  "itemCount": 1,
  "items": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440200",
      "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
      "exerciseName": "Bench Press",
      "exerciseImageUrl": "https://storage.example.com/exercises/bench.jpg",
      "sets": 4,
      "reps": "8-10",
      "load": 60.0,
      "restTime": "90s",
      "sequenceOrder": 1
    }
  ],
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

**Possible Errors:**
- `401` - Authentication required
- `404` - Routine not found

---

### PUT `/api/v1/routines/{id}`
Update a routine.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Routine ID |

**Request Body:**
```json
{
  "name": "Push Day Routine (Updated)",
  "description": "Updated description",
  "expirationDate": "2025-07-01T00:00:00Z",
  "memberId": "550e8400-e29b-41d4-a716-446655440002",
  "items": [
    {
      "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
      "sets": 5,
      "reps": "6-8",
      "load": 70.0,
      "restTime": "120s",
      "sequenceOrder": 1
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440100",
  "name": "Push Day Routine (Updated)",
  "description": "Updated description",
  "expirationDate": "2025-07-01T00:00:00Z",
  "isExpired": false,
  "creatorId": "550e8400-e29b-41d4-a716-446655440001",
  "memberId": "550e8400-e29b-41d4-a716-446655440002",
  "itemCount": 1,
  "items": [...],
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T12:00:00"
}
```

**Possible Errors:**
- `400` - Validation failed
- `401` - Authentication required
- `403` - Access denied
- `404` - Routine or exercise not found

---

### DELETE `/api/v1/routines/{id}`
Delete a routine.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Routine ID |

**Response (204 No Content):** Empty body

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied
- `404` - Routine not found

---

### POST `/api/v1/routines/{id}/associate`
Associate a routine with a member.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | Yes | Routine ID |

**Request Body:**
```json
{
  "memberId": "550e8400-e29b-41d4-a716-446655440002"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| memberId | UUID | Yes | Not null |

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Validation failed
- `401` - Authentication required
- `403` - Access denied
- `404` - Routine or member not found

---

## Member Endpoints

### GET `/api/v1/member/routines`
Get authenticated member's routines.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440100",
    "name": "Push Day Routine",
    "description": "Chest, shoulders, and triceps workout",
    "expirationDate": "2025-06-01T00:00:00Z",
    "isExpired": false,
    "creatorId": "550e8400-e29b-41d4-a716-446655440001",
    "items": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440200",
        "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
        "exerciseName": "Bench Press",
        "exerciseImageUrl": "https://storage.example.com/exercises/bench.jpg",
        "sets": 4,
        "reps": "8-10",
        "load": 60.0,
        "restTime": "90s",
        "sequenceOrder": 1
      }
    ],
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
]
```

**Possible Errors:**
- `401` - Authentication required

---

### POST `/api/v1/member/workouts`
Log a completed workout session.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Request Body:**
```json
{
  "routineId": "550e8400-e29b-41d4-a716-446655440100",
  "startedAt": "2025-01-15T09:00:00Z",
  "endedAt": "2025-01-15T10:15:00Z",
  "items": [
    {
      "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
      "setsDone": 4,
      "repsDone": 10,
      "loadUsed": 60.0
    }
  ]
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| routineId | UUID | No | - |
| startedAt | ISO timestamp | Yes | Not null |
| endedAt | ISO timestamp | No | - |
| items | array | Yes | At least one item required |

**Workout Item:**
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| exerciseId | UUID | Yes | Not null |
| setsDone | integer | Yes | Min 1 |
| repsDone | integer | Yes | Min 1 |
| loadUsed | double | No | Min 0 |

**Response (201 Created):**
```json
{
  "workoutSessionId": "550e8400-e29b-41d4-a716-446655440300",
  "adherenceRate": 87.5,
  "message": "Workout logged successfully"
}
```

**Possible Errors:**
- `400` - Validation failed
- `401` - Authentication required
- `404` - Exercise or routine not found

---

## Report Endpoints

### GET `/api/v1/reports/members/{memberId}`
Get performance report for a member.

**Headers:**
```
Authorization: Bearer <auth_token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| memberId | UUID | Yes | Member's user ID |

**Response (200 OK):**
```json
{
  "memberId": "550e8400-e29b-41d4-a716-446655440002",
  "memberName": "Alice Member",
  "memberEmail": "alice@example.com",
  "adherenceRate": 85.5,
  "workoutHistory": [
    {
      "workoutSessionId": "550e8400-e29b-41d4-a716-446655440300",
      "date": "2025-01-15T09:00:00Z",
      "routineName": "Push Day Routine",
      "durationMinutes": 75
    }
  ]
}
```

**Possible Errors:**
- `401` - Authentication required
- `403` - Access denied (can only view own report or assigned members)
- `404` - Member not found

---

## Password Reset Endpoints

### POST `/api/v1/password-reset/request`
Request a password reset email.

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| email | string | Yes | Valid email format |

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Validation failed (invalid email format)

> Note: For security reasons, always returns 200 even if email doesn't exist.

---

### POST `/api/v1/password-reset/validate-reset`
Validate a password reset token.

**Headers:**
```
Authorization: Bearer <reset_token>
```

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Invalid or expired token

---

### POST `/api/v1/password-reset/new-password`
Set a new password using reset token.

**Headers:**
```
Authorization: Bearer <reset_token>
```

**Request Body:**
```json
{
  "newPassword": "newSecurePassword123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| newPassword | string | Yes | 8-72 characters |

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Validation failed, invalid or expired token

---

## Onboarding Endpoints

### POST `/api/v1/onboarding/validate-onboarding`
Validate an onboarding token.

**Headers:**
```
Authorization: Bearer <onboarding_token>
```

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Invalid or expired token

---

### POST `/api/v1/onboarding/activate-account`
Activate user account and set password.

**Headers:**
```
Authorization: Bearer <onboarding_token>
```

**Request Body:**
```json
{
  "password": "securePassword123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| password | string | Yes | 8-72 characters |

**Response (200 OK):** Empty body

**Possible Errors:**
- `400` - Validation failed, invalid or expired token

## Building for Production

```bash
# Build the JAR
./mvnw clean package -DskipTests

# Run the JAR with production profile
java -jar target/routine-revo-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Testing

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report
```

## Technologies Used

- **Spring Boot 3.5.7** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **JWT (jjwt)** - Token-based authentication
- **AWS SES** - Email service
- **Thymeleaf** - Email template engine
- **Lombok** - Boilerplate code reduction
- **BCrypt** - Password hashing

## Database Management

### Cleaning All Data in the Database

There are several ways to clean/reset your database for local development:

#### Method 1: Drop and Recreate Database (Recommended) ⭐

This is the most reliable method:

```bash
# Connect to PostgreSQL
psql -U postgres -h localhost

# Drop the database
DROP DATABASE IF EXISTS routine_revo_db;

# Recreate it
CREATE DATABASE routine_revo_db;

# Exit psql
\q

# Then start your application (Flyway will run migrations automatically)
./start-local.sh
```

#### Method 2: Using the Reset Script 🆕

The easiest way - just run the interactive script:

```bash
./reset-db.sh
```

This provides a menu with options:
1. Drop and recreate database (recommended)
2. Docker reset (for Docker Compose setup)
3. Manual truncate (keeps schema, deletes data only)
4. Cancel

#### Method 3: Using Docker Compose

If you're running PostgreSQL in Docker:

```bash
# Stop and remove containers with volumes
docker-compose down -v

# Start fresh
docker-compose up -d

# Wait a few seconds for database to be ready, then start application
sleep 5
./start-local.sh
```

#### Method 4: Manual Table Truncation

If you only want to delete data but keep the schema:

```bash
# Connect to the database
psql -U postgres -h localhost -d routine_revo_db

# Disable triggers temporarily
SET session_replication_role = 'replica';

# Truncate all tables (adjust table names as needed)
TRUNCATE TABLE users CASCADE;
-- Add other tables here as your schema grows

# Re-enable triggers
SET session_replication_role = 'origin';

# Delete Flyway history for seed migration (so it runs again)
DELETE FROM flyway_schema_history WHERE version = '4';

# Exit
\q

# Restart application to re-seed data
./start-local.sh
```

#### Method 5: Only Re-seed Data (Without Schema Changes)

If you want to keep the schema but refresh seed data:

```bash
# Connect to database
psql -U postgres -h localhost -d routine_revo_db

# Delete only the seeded users (with specific UUIDs)
DELETE FROM users WHERE id IN (
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000006',
    '00000000-0000-0000-0000-000000000007'
);

# Remove the seed migration from history so it runs again
DELETE FROM flyway_schema_history WHERE version = '4';

# Exit
\q

# Restart application - the seed migration will run again
./start-local.sh
```

### Quick Reference Commands

```bash
# Using the interactive script (easiest)
./reset-db.sh

# Full reset with psql
psql -U postgres -h localhost -c "DROP DATABASE IF EXISTS routine_revo_db;" && \
psql -U postgres -h localhost -c "CREATE DATABASE routine_revo_db;" && \
./start-local.sh

# Docker reset
docker-compose down -v && docker-compose up -d && sleep 5 && ./start-local.sh
```

### Automatic Data Seeding

Remember that with the `local` profile active (default), the application will automatically:
1. Run Flyway migrations (including V4 seed data)
2. Run the `LocalDataSeeder` component (but only if no users exist)

See [LOCAL_SEEDING.md](LOCAL_SEEDING.md) for more details on data seeding.

## Common Issues

### Database Connection Error
```
Cannot connect to database
```
**Solution**: Make sure PostgreSQL is running and the database exists. If using Docker: `docker-compose up -d`

### Flyway Validation Failed
```
Validate failed: Migrations have failed validation
```
**Solution**: 
- For local dev: `./mvnw flyway:clean` (⚠️ deletes all data)
- For prod: Check migration files match database schema

### Email Sending Fails
```
AWS SES authentication failed
```
**Solution**: Verify AWS credentials in `local.env` are correct and have SES permissions

## Contributing

1. Create a feature branch: `git checkout -b feature/my-feature`
2. Make your changes
3. Test thoroughly
4. Commit with clear messages: `git commit -m "Add: my feature"`
5. Push and create a Pull Request

## License

This project is proprietary and confidential.

## Support

For questions or issues, contact the development team.

---

**Happy coding! 🚀**

