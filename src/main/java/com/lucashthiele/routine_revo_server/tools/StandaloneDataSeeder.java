package com.lucashthiele.routine_revo_server.tools;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class StandaloneDataSeeder {
  
  private static final String DB_URL = "jdbc:postgresql://localhost:5432/routine_revo_db";
  private static final String DB_USER = "postgres";
  private static final String DB_PASSWORD = "postgres";
  
  // BCrypt configuration - 10 rounds is a good balance between security and performance
  private static final int BCRYPT_ROUNDS = 10;
  
  // Predefined UUIDs for consistent test data
  // Users
  private static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID JOHN_COACH_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID SARAH_COACH_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final UUID ALICE_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
  private static final UUID BOB_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
  private static final UUID CHARLIE_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
  private static final UUID DAVID_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
  
  // Exercises
  private static final UUID EXERCISE_BENCH_PRESS = UUID.fromString("10000000-0000-0000-0000-000000000001");
  private static final UUID EXERCISE_PUSH_UP = UUID.fromString("10000000-0000-0000-0000-000000000002");
  private static final UUID EXERCISE_CABLE_FLY = UUID.fromString("10000000-0000-0000-0000-000000000003");
  private static final UUID EXERCISE_DEADLIFT = UUID.fromString("10000000-0000-0000-0000-000000000004");
  private static final UUID EXERCISE_PULL_UP = UUID.fromString("10000000-0000-0000-0000-000000000005");
  private static final UUID EXERCISE_BARBELL_ROW = UUID.fromString("10000000-0000-0000-0000-000000000006");
  private static final UUID EXERCISE_SHOULDER_PRESS = UUID.fromString("10000000-0000-0000-0000-000000000007");
  private static final UUID EXERCISE_LATERAL_RAISE = UUID.fromString("10000000-0000-0000-0000-000000000008");
  private static final UUID EXERCISE_FACE_PULL = UUID.fromString("10000000-0000-0000-0000-000000000009");
  private static final UUID EXERCISE_BARBELL_CURL = UUID.fromString("10000000-0000-0000-0000-000000000010");
  private static final UUID EXERCISE_HAMMER_CURL = UUID.fromString("10000000-0000-0000-0000-000000000011");
  private static final UUID EXERCISE_TRICEP_DIPS = UUID.fromString("10000000-0000-0000-0000-000000000012");
  private static final UUID EXERCISE_TRICEP_PUSHDOWN = UUID.fromString("10000000-0000-0000-0000-000000000013");
  private static final UUID EXERCISE_SQUAT = UUID.fromString("10000000-0000-0000-0000-000000000014");
  private static final UUID EXERCISE_LEG_PRESS = UUID.fromString("10000000-0000-0000-0000-000000000015");
  private static final UUID EXERCISE_LUNGES = UUID.fromString("10000000-0000-0000-0000-000000000016");
  private static final UUID EXERCISE_HIP_THRUST = UUID.fromString("10000000-0000-0000-0000-000000000017");
  private static final UUID EXERCISE_GLUTE_BRIDGE = UUID.fromString("10000000-0000-0000-0000-000000000018");
  private static final UUID EXERCISE_PLANK = UUID.fromString("10000000-0000-0000-0000-000000000019");
  private static final UUID EXERCISE_CRUNCHES = UUID.fromString("10000000-0000-0000-0000-000000000020");
  private static final UUID EXERCISE_RUNNING = UUID.fromString("10000000-0000-0000-0000-000000000021");
  private static final UUID EXERCISE_CYCLING = UUID.fromString("10000000-0000-0000-0000-000000000022");
  private static final UUID EXERCISE_BURPEES = UUID.fromString("10000000-0000-0000-0000-000000000023");
  
  // Routines
  private static final UUID ROUTINE_UPPER_BODY = UUID.fromString("20000000-0000-0000-0000-000000000001");
  private static final UUID ROUTINE_LOWER_BODY = UUID.fromString("20000000-0000-0000-0000-000000000002");
  private static final UUID ROUTINE_FULL_BODY = UUID.fromString("20000000-0000-0000-0000-000000000003");
  private static final UUID ROUTINE_CARDIO_ABS = UUID.fromString("20000000-0000-0000-0000-000000000004");
  
  public static void main(String[] args) {
    System.out.println("========================================");
    System.out.println("  Routine Revo - Standalone Data Seeder");
    System.out.println("========================================");
    System.out.println();
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
      System.out.println("✓ Successfully connected to database");
      
      // Check if data already exists
      if (hasExistingData(conn)) {
        System.out.println("⚠ Data already exists in the database.");
        
        // Check for --force flag to skip confirmation
        boolean force = args.length > 0 && "--force".equals(args[0]);
        
        if (!force) {
          System.out.print("Do you want to clear existing data and reseed? (yes/no): ");
          
          String response;
          if (System.console() != null) {
            response = System.console().readLine();
          } else {
            // Non-interactive mode - read from stdin
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            response = scanner.hasNextLine() ? scanner.nextLine() : "no";
          }
          
          if (!"yes".equalsIgnoreCase(response.trim())) {
            System.out.println("✗ Seeding cancelled. Use --force flag to skip confirmation.");
            return;
          }
        } else {
          System.out.println("Force mode enabled - clearing existing data...");
        }
        
        clearExistingData(conn);
        System.out.println("✓ Existing data cleared");
      }
      
      System.out.println("Starting data seeding...");
      System.out.println();
      
      seedUsers(conn);
      seedExercises(conn);
      seedRoutines(conn);
      
      System.out.println();
      System.out.println("========================================");
      System.out.println("  ✓ Data seeding completed successfully!");
      System.out.println("========================================");
      System.out.println();
      System.out.println("Test Users:");
      System.out.println("  Admin:");
      System.out.println("    Email: admin@routinerevo.com");
      System.out.println("    Password: admin123");
      System.out.println();
      System.out.println("  Coaches:");
      System.out.println("    Email: john.coach@routinerevo.com");
      System.out.println("    Password: coach123");
      System.out.println();
      System.out.println("    Email: sarah.coach@routinerevo.com");
      System.out.println("    Password: coach123");
      System.out.println();
      System.out.println("  Members:");
      System.out.println("    Email: alice.member@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println();
      System.out.println("    Email: bob.member@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println();
      System.out.println("Exercises:");
      System.out.println("  23 exercises created across all muscle groups:");
      System.out.println("    - 3 Chest exercises");
      System.out.println("    - 3 Back exercises");
      System.out.println("    - 3 Shoulder exercises");
      System.out.println("    - 2 Biceps exercises");
      System.out.println("    - 2 Triceps exercises");
      System.out.println("    - 3 Leg exercises");
      System.out.println("    - 2 Glute exercises");
      System.out.println("    - 2 Ab exercises");
      System.out.println("    - 2 Cardio exercises");
      System.out.println("    - 1 Full Body exercise");
      System.out.println();
      System.out.println("Routines:");
      System.out.println("  4 sample routines created and assigned to members:");
      System.out.println("    - Upper Body Routine (Alice)");
      System.out.println("    - Lower Body Routine (Alice)");
      System.out.println("    - Full Body Routine (Bob)");
      System.out.println("    - Cardio & Abs (Bob)");
      System.out.println();
      
    } catch (SQLException e) {
      System.err.println("✗ Database error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    } catch (Exception e) {
      System.err.println("✗ Unexpected error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  private static boolean hasExistingData(Connection conn) throws SQLException {
    String sql = "SELECT COUNT(*) FROM users";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
    }
    return false;
  }
  
  private static boolean tableExists(Connection conn, String tableName) throws SQLException {
    String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, tableName);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getBoolean(1);
        }
      }
    }
    return false;
  }
  
  private static void clearExistingData(Connection conn) throws SQLException {
    // Delete in correct order to respect foreign key constraints
    if (tableExists(conn, "routine_items")) {
      String sqlRoutineItems = "DELETE FROM routine_items";
      try (PreparedStatement stmt = conn.prepareStatement(sqlRoutineItems)) {
        stmt.executeUpdate();
      }
    }
    
    if (tableExists(conn, "routines")) {
      String sqlRoutines = "DELETE FROM routines";
      try (PreparedStatement stmt = conn.prepareStatement(sqlRoutines)) {
        stmt.executeUpdate();
      }
    }
    
    if (tableExists(conn, "exercises")) {
      String sqlExercises = "DELETE FROM exercises";
      try (PreparedStatement stmt = conn.prepareStatement(sqlExercises)) {
        stmt.executeUpdate();
      }
    }
    
    String sqlUsers = "DELETE FROM users";
    try (PreparedStatement stmt = conn.prepareStatement(sqlUsers)) {
      stmt.executeUpdate();
    }
  }
  
  private static void seedUsers(Connection conn) throws SQLException {
    System.out.println("Seeding users...");
    
    // Disable auto-commit for transaction
    conn.setAutoCommit(false);
    
    try {
      // Create Admin User
      insertUser(conn, ADMIN_ID, "Admin User", "admin@routinerevo.com", 
          hashPassword("admin123"), "ADMIN", "ACTIVE", null, null);
      System.out.println("  ✓ Created admin user: admin@routinerevo.com");
      
      // Create Coach Users
      insertUser(conn, JOHN_COACH_ID, "John Coach", "john.coach@routinerevo.com",
          hashPassword("coach123"), "COACH", "ACTIVE", null, null);
      System.out.println("  ✓ Created coach user: john.coach@routinerevo.com");
      
      insertUser(conn, SARAH_COACH_ID, "Sarah Coach", "sarah.coach@routinerevo.com",
          hashPassword("coach123"), "COACH", "ACTIVE", null, null);
      System.out.println("  ✓ Created coach user: sarah.coach@routinerevo.com");
      
      // Create Member Users
      insertUser(conn, ALICE_MEMBER_ID, "Alice Member", "alice.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", JOHN_COACH_ID, 3);
      System.out.println("  ✓ Created member user: alice.member@routinerevo.com");
      
      insertUser(conn, BOB_MEMBER_ID, "Bob Member", "bob.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", JOHN_COACH_ID, 4);
      System.out.println("  ✓ Created member user: bob.member@routinerevo.com");
      
      insertUser(conn, CHARLIE_MEMBER_ID, "Charlie Member", "charlie.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "PENDING", SARAH_COACH_ID, 5);
      System.out.println("  ✓ Created pending member user: charlie.member@routinerevo.com");
      
      insertUser(conn, DAVID_MEMBER_ID, "David Member", "david.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "INACTIVE", SARAH_COACH_ID, 3);
      System.out.println("  ✓ Created inactive member user: david.member@routinerevo.com");
      
      // Commit transaction
      conn.commit();
      System.out.println("  ✓ Successfully seeded 7 users");
      
    } catch (SQLException e) {
      // Rollback on error
      conn.rollback();
      throw e;
    } finally {
      conn.setAutoCommit(true);
    }
  }
  
  private static void insertUser(
      Connection conn,
      UUID id,
      String name,
      String email,
      String hashedPassword,
      String role,
      String status,
      UUID coachId,
      Integer workoutPerWeek
  ) throws SQLException {
    String sql = "INSERT INTO users (id, name, email, hashed_password, role, status, coach_id, workout_per_week, created_at, updated_at) " +
                 "VALUES (?, ?, ?, ?, ?::user_role, ?::user_status, ?, ?, ?, ?)";
    
    LocalDateTime now = LocalDateTime.now();
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setString(2, name);
      stmt.setString(3, email);
      stmt.setString(4, hashedPassword);
      stmt.setString(5, role);
      stmt.setString(6, status);
      stmt.setObject(7, coachId);
      if (workoutPerWeek != null) {
        stmt.setInt(8, workoutPerWeek);
      } else {
        stmt.setNull(8, java.sql.Types.INTEGER);
      }
      stmt.setTimestamp(9, Timestamp.valueOf(now));
      stmt.setTimestamp(10, Timestamp.valueOf(now));
      
      stmt.executeUpdate();
    }
  }
  
  private static String hashPassword(String plainPassword) {
    System.out.println("    Hashing password with BCrypt (rounds=" + BCRYPT_ROUNDS + ")...");
    String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    System.out.println("    ✓ Password hashed");
    return hashed;
  }
  
  private static void seedExercises(Connection conn) throws SQLException {
    if (!tableExists(conn, "exercises")) {
      System.out.println("⚠ Exercises table does not exist yet. Skipping exercise seeding.");
      System.out.println("  Run Flyway migration V4 first to create the exercises table.");
      return;
    }
    
    System.out.println("Seeding exercises...");
    
    // Disable auto-commit for transaction
    conn.setAutoCommit(false);
    
    try {
      // CHEST exercises
      insertExercise(conn, EXERCISE_BENCH_PRESS, "Bench Press", "CHEST",
          "Compound chest exercise performed lying on a bench with a barbell",
          "Barbell, Bench", null);
      System.out.println("  ✓ Created exercise: Bench Press");
      
      insertExercise(conn, EXERCISE_PUSH_UP, "Push-up", "CHEST",
          "Bodyweight exercise for chest, shoulders, and triceps",
          "None (Bodyweight)", null);
      System.out.println("  ✓ Created exercise: Push-up");
      
      insertExercise(conn, EXERCISE_CABLE_FLY, "Cable Fly", "CHEST",
          "Isolation exercise targeting the chest using cable machine",
          "Cable Machine", null);
      System.out.println("  ✓ Created exercise: Cable Fly");
      
      // BACK exercises
      insertExercise(conn, EXERCISE_DEADLIFT, "Deadlift", "BACK",
          "Compound exercise targeting back, legs, and core",
          "Barbell", null);
      System.out.println("  ✓ Created exercise: Deadlift");
      
      insertExercise(conn, EXERCISE_PULL_UP, "Pull-up", "BACK",
          "Bodyweight exercise for back and biceps strength",
          "Pull-up Bar", null);
      System.out.println("  ✓ Created exercise: Pull-up");
      
      insertExercise(conn, EXERCISE_BARBELL_ROW, "Barbell Row", "BACK",
          "Compound rowing exercise for back thickness",
          "Barbell", null);
      System.out.println("  ✓ Created exercise: Barbell Row");
      
      // SHOULDERS exercises
      insertExercise(conn, EXERCISE_SHOULDER_PRESS, "Shoulder Press", "SHOULDERS",
          "Overhead pressing movement for shoulder development",
          "Barbell or Dumbbells", null);
      System.out.println("  ✓ Created exercise: Shoulder Press");
      
      insertExercise(conn, EXERCISE_LATERAL_RAISE, "Lateral Raise", "SHOULDERS",
          "Isolation exercise for side deltoids",
          "Dumbbells", null);
      System.out.println("  ✓ Created exercise: Lateral Raise");
      
      insertExercise(conn, EXERCISE_FACE_PULL, "Face Pull", "SHOULDERS",
          "Cable exercise targeting rear deltoids and upper back",
          "Cable Machine with Rope", null);
      System.out.println("  ✓ Created exercise: Face Pull");
      
      // BICEPS exercises
      insertExercise(conn, EXERCISE_BARBELL_CURL, "Barbell Curl", "BICEPS",
          "Classic bicep exercise with a barbell",
          "Barbell", null);
      System.out.println("  ✓ Created exercise: Barbell Curl");
      
      insertExercise(conn, EXERCISE_HAMMER_CURL, "Hammer Curl", "BICEPS",
          "Dumbbell curl with neutral grip for biceps and forearms",
          "Dumbbells", null);
      System.out.println("  ✓ Created exercise: Hammer Curl");
      
      // TRICEPS exercises
      insertExercise(conn, EXERCISE_TRICEP_DIPS, "Tricep Dips", "TRICEPS",
          "Bodyweight or weighted exercise for tricep development",
          "Dip Bars or Bench", null);
      System.out.println("  ✓ Created exercise: Tricep Dips");
      
      insertExercise(conn, EXERCISE_TRICEP_PUSHDOWN, "Tricep Pushdown", "TRICEPS",
          "Cable isolation exercise for triceps",
          "Cable Machine", null);
      System.out.println("  ✓ Created exercise: Tricep Pushdown");
      
      // LEGS exercises
      insertExercise(conn, EXERCISE_SQUAT, "Squat", "LEGS",
          "King of all exercises - compound movement for legs and core",
          "Barbell, Squat Rack", null);
      System.out.println("  ✓ Created exercise: Squat");
      
      insertExercise(conn, EXERCISE_LEG_PRESS, "Leg Press", "LEGS",
          "Machine-based compound exercise for quadriceps and glutes",
          "Leg Press Machine", null);
      System.out.println("  ✓ Created exercise: Leg Press");
      
      insertExercise(conn, EXERCISE_LUNGES, "Lunges", "LEGS",
          "Unilateral leg exercise for balance and strength",
          "Dumbbells (optional)", null);
      System.out.println("  ✓ Created exercise: Lunges");
      
      // GLUTES exercises
      insertExercise(conn, EXERCISE_HIP_THRUST, "Hip Thrust", "GLUTES",
          "Premier exercise for glute development and strength",
          "Barbell, Bench", null);
      System.out.println("  ✓ Created exercise: Hip Thrust");
      
      insertExercise(conn, EXERCISE_GLUTE_BRIDGE, "Glute Bridge", "GLUTES",
          "Bodyweight or weighted exercise targeting glutes",
          "None or Barbell", null);
      System.out.println("  ✓ Created exercise: Glute Bridge");
      
      // ABS exercises
      insertExercise(conn, EXERCISE_PLANK, "Plank", "ABS",
          "Isometric core strengthening exercise",
          "None (Bodyweight)", null);
      System.out.println("  ✓ Created exercise: Plank");
      
      insertExercise(conn, EXERCISE_CRUNCHES, "Crunches", "ABS",
          "Classic abdominal exercise targeting rectus abdominis",
          "None (Bodyweight)", null);
      System.out.println("  ✓ Created exercise: Crunches");
      
      // CARDIO exercises
      insertExercise(conn, EXERCISE_RUNNING, "Running", "CARDIO",
          "Cardiovascular exercise for endurance and fat loss",
          "Treadmill or Outdoors", null);
      System.out.println("  ✓ Created exercise: Running");
      
      insertExercise(conn, EXERCISE_CYCLING, "Cycling", "CARDIO",
          "Low-impact cardio exercise for endurance",
          "Bike or Stationary Bike", null);
      System.out.println("  ✓ Created exercise: Cycling");
      
      // FULL_BODY exercises
      insertExercise(conn, EXERCISE_BURPEES, "Burpees", "FULL_BODY",
          "High-intensity full body exercise combining squat, push-up, and jump",
          "None (Bodyweight)", null);
      System.out.println("  ✓ Created exercise: Burpees");
      
      // Commit transaction
      conn.commit();
      System.out.println("  ✓ Successfully seeded 23 exercises");
      
    } catch (SQLException e) {
      // Rollback on error
      conn.rollback();
      throw e;
    } finally {
      conn.setAutoCommit(true);
    }
  }
  
  private static void insertExercise(
      Connection conn,
      UUID id,
      String name,
      String muscleGroup,
      String description,
      String equipment,
      String imageUrl
  ) throws SQLException {
    String sql = "INSERT INTO exercises (id, name, muscle_group, description, equipment, image_url, created_at, updated_at) " +
                 "VALUES (?, ?, ?::muscle_group, ?, ?, ?, ?, ?)";
    
    LocalDateTime now = LocalDateTime.now();
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setString(2, name);
      stmt.setString(3, muscleGroup);
      stmt.setString(4, description);
      stmt.setString(5, equipment);
      stmt.setString(6, imageUrl);
      stmt.setTimestamp(7, Timestamp.valueOf(now));
      stmt.setTimestamp(8, Timestamp.valueOf(now));
      
      stmt.executeUpdate();
    }
  }
  
  private static void seedRoutines(Connection conn) throws SQLException {
    if (!tableExists(conn, "routines")) {
      System.out.println("⚠ Routines table does not exist yet. Skipping routine seeding.");
      System.out.println("  Run Flyway migration V5 first to create the routines tables.");
      return;
    }
    
    System.out.println("Seeding routines...");
    
    // Disable auto-commit for transaction
    conn.setAutoCommit(false);
    
    try {
      // Calculate expiration dates (30 days from now)
      LocalDateTime expirationDate = LocalDateTime.now().plusDays(30);
      
      // ROUTINE 1: Upper Body Routine for Alice (created by John Coach)
      insertRoutine(conn, ROUTINE_UPPER_BODY, "Upper Body Strength",
          "Complete upper body workout focusing on chest, back, and shoulders",
          expirationDate, JOHN_COACH_ID, ALICE_MEMBER_ID);
      System.out.println("  ✓ Created routine: Upper Body Strength");
      
      // Add items to Upper Body Routine
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_UPPER_BODY, EXERCISE_BENCH_PRESS,
          4, "8-10", 60.0, "90 sec", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_UPPER_BODY, EXERCISE_BARBELL_ROW,
          4, "8-10", 55.0, "90 sec", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_UPPER_BODY, EXERCISE_SHOULDER_PRESS,
          3, "10-12", 40.0, "60 sec", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_UPPER_BODY, EXERCISE_LATERAL_RAISE,
          3, "12-15", 10.0, "45 sec", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_UPPER_BODY, EXERCISE_BARBELL_CURL,
          3, "10-12", 25.0, "60 sec", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_UPPER_BODY, EXERCISE_TRICEP_PUSHDOWN,
          3, "12-15", null, "45 sec", 6);
      System.out.println("    ✓ Added 6 exercises to Upper Body Strength");
      
      // ROUTINE 2: Lower Body Routine for Alice (created by John Coach)
      insertRoutine(conn, ROUTINE_LOWER_BODY, "Lower Body Power",
          "Leg day focusing on strength and muscle development",
          expirationDate, JOHN_COACH_ID, ALICE_MEMBER_ID);
      System.out.println("  ✓ Created routine: Lower Body Power");
      
      // Add items to Lower Body Routine
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_LOWER_BODY, EXERCISE_SQUAT,
          5, "5-8", 80.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_LOWER_BODY, EXERCISE_LEG_PRESS,
          4, "10-12", 120.0, "90 sec", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_LOWER_BODY, EXERCISE_LUNGES,
          3, "12-15/leg", 15.0, "60 sec", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_LOWER_BODY, EXERCISE_HIP_THRUST,
          4, "10-12", 70.0, "90 sec", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_LOWER_BODY, EXERCISE_GLUTE_BRIDGE,
          3, "15-20", 40.0, "45 sec", 5);
      System.out.println("    ✓ Added 5 exercises to Lower Body Power");
      
      // ROUTINE 3: Full Body Routine for Bob (created by John Coach)
      insertRoutine(conn, ROUTINE_FULL_BODY, "Full Body Circuit",
          "Complete full body workout for overall fitness",
          expirationDate, JOHN_COACH_ID, BOB_MEMBER_ID);
      System.out.println("  ✓ Created routine: Full Body Circuit");
      
      // Add items to Full Body Routine
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_FULL_BODY, EXERCISE_DEADLIFT,
          4, "6-8", 90.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_FULL_BODY, EXERCISE_BENCH_PRESS,
          3, "8-10", 55.0, "90 sec", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_FULL_BODY, EXERCISE_SQUAT,
          4, "8-10", 70.0, "2 min", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_FULL_BODY, EXERCISE_PULL_UP,
          3, "AMRAP", null, "90 sec", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_FULL_BODY, EXERCISE_SHOULDER_PRESS,
          3, "10-12", 35.0, "60 sec", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_FULL_BODY, EXERCISE_PLANK,
          3, "45-60 sec", null, "60 sec", 6);
      System.out.println("    ✓ Added 6 exercises to Full Body Circuit");
      
      // ROUTINE 4: Cardio & Abs for Bob (created by John Coach)
      insertRoutine(conn, ROUTINE_CARDIO_ABS, "Cardio & Core Blast",
          "High intensity cardio and core strengthening routine",
          expirationDate, JOHN_COACH_ID, BOB_MEMBER_ID);
      System.out.println("  ✓ Created routine: Cardio & Core Blast");
      
      // Add items to Cardio & Abs Routine
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_CARDIO_ABS, EXERCISE_RUNNING,
          1, "20 min", null, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_CARDIO_ABS, EXERCISE_BURPEES,
          3, "15-20", null, "60 sec", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_CARDIO_ABS, EXERCISE_PLANK,
          4, "45-60 sec", null, "45 sec", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_CARDIO_ABS, EXERCISE_CRUNCHES,
          4, "20-25", null, "45 sec", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_CARDIO_ABS, EXERCISE_CYCLING,
          1, "15 min", null, "done", 5);
      System.out.println("    ✓ Added 5 exercises to Cardio & Core Blast");
      
      // Commit transaction
      conn.commit();
      System.out.println("  ✓ Successfully seeded 4 routines with 22 total exercises");
      
    } catch (SQLException e) {
      // Rollback on error
      conn.rollback();
      throw e;
    } finally {
      conn.setAutoCommit(true);
    }
  }
  
  private static void insertRoutine(
      Connection conn,
      UUID id,
      String name,
      String description,
      LocalDateTime expirationDate,
      UUID creatorId,
      UUID memberId
  ) throws SQLException {
    String sql = "INSERT INTO routines (id, name, description, expiration_date, creator_id, member_id, created_at, updated_at) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    LocalDateTime now = LocalDateTime.now();
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setString(2, name);
      stmt.setString(3, description);
      stmt.setTimestamp(4, expirationDate != null ? Timestamp.valueOf(expirationDate) : null);
      stmt.setObject(5, creatorId);
      stmt.setObject(6, memberId);
      stmt.setTimestamp(7, Timestamp.valueOf(now));
      stmt.setTimestamp(8, Timestamp.valueOf(now));
      
      stmt.executeUpdate();
    }
  }
  
  private static void insertRoutineItem(
      Connection conn,
      UUID id,
      UUID routineId,
      UUID exerciseId,
      Integer sets,
      String reps,
      Double load,
      String restTime,
      Integer sequenceOrder
  ) throws SQLException {
    String sql = "INSERT INTO routine_items (id, routine_id, exercise_id, sets, reps, load, rest_time, sequence_order) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setObject(2, routineId);
      stmt.setObject(3, exerciseId);
      stmt.setInt(4, sets);
      stmt.setString(5, reps);
      if (load != null) {
        stmt.setDouble(6, load);
      } else {
        stmt.setNull(6, java.sql.Types.DOUBLE);
      }
      stmt.setString(7, restTime);
      stmt.setInt(8, sequenceOrder);
      
      stmt.executeUpdate();
    }
  }
}

