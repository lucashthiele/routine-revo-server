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

### Authentication Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - User logout

### User Endpoints

- `GET /api/users` - List users (with pagination and filtering)
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Password Reset Endpoints

- `POST /api/password-reset/request` - Request password reset
- `POST /api/password-reset/confirm` - Confirm password reset

### Onboarding Endpoints

- `POST /api/onboarding/request` - Request onboarding
- `POST /api/onboarding/confirm` - Confirm onboarding

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

