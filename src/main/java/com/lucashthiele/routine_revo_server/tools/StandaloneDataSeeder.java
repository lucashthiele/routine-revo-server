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
  // Unassigned members (no coach, managed by admin)
  private static final UUID EVA_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000008");
  private static final UUID FRANK_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");
  private static final UUID GRACE_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
  
  // Membros brasileiros (clientes em português, gerenciados pelo admin)
  private static final UUID JOAO_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
  private static final UUID MARIA_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
  private static final UUID PEDRO_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000013");
  private static final UUID ANA_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000014");
  private static final UUID LUCAS_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000015");
  private static final UUID JULIA_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000016");
  private static final UUID RAFAEL_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000017");
  private static final UUID CAROLINA_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000018");
  private static final UUID GUSTAVO_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000019");
  private static final UUID FERNANDA_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
  
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
  
  // Routine Templates (reusable, no member assigned)
  private static final UUID TEMPLATE_UPPER_BODY = UUID.fromString("20000000-0000-0000-0000-000000000001");
  private static final UUID TEMPLATE_LOWER_BODY = UUID.fromString("20000000-0000-0000-0000-000000000002");
  private static final UUID TEMPLATE_FULL_BODY = UUID.fromString("20000000-0000-0000-0000-000000000003");
  private static final UUID TEMPLATE_CARDIO_ABS = UUID.fromString("20000000-0000-0000-0000-000000000004");
  private static final UUID TEMPLATE_PUSH_DAY = UUID.fromString("20000000-0000-0000-0000-000000000005");
  private static final UUID TEMPLATE_PULL_DAY = UUID.fromString("20000000-0000-0000-0000-000000000006");
  
  // Custom Routines (assigned to specific members, copied from templates)
  private static final UUID ROUTINE_ALICE_UPPER = UUID.fromString("20000000-0000-0000-0000-000000000101");
  private static final UUID ROUTINE_ALICE_LOWER = UUID.fromString("20000000-0000-0000-0000-000000000102");
  private static final UUID ROUTINE_BOB_FULL = UUID.fromString("20000000-0000-0000-0000-000000000103");
  private static final UUID ROUTINE_BOB_CARDIO = UUID.fromString("20000000-0000-0000-0000-000000000104");
  // Brazilian members routines
  private static final UUID ROUTINE_JOAO_UPPER = UUID.fromString("20000000-0000-0000-0000-000000000201");
  private static final UUID ROUTINE_JOAO_LOWER = UUID.fromString("20000000-0000-0000-0000-000000000202");
  private static final UUID ROUTINE_MARIA_FULL = UUID.fromString("20000000-0000-0000-0000-000000000203");
  private static final UUID ROUTINE_MARIA_CARDIO = UUID.fromString("20000000-0000-0000-0000-000000000204");
  private static final UUID ROUTINE_PEDRO_PUSH = UUID.fromString("20000000-0000-0000-0000-000000000205");
  private static final UUID ROUTINE_PEDRO_PULL = UUID.fromString("20000000-0000-0000-0000-000000000206");
  
  // Workout Sessions
  private static final UUID WORKOUT_SESSION_1 = UUID.fromString("30000000-0000-0000-0000-000000000001");
  private static final UUID WORKOUT_SESSION_2 = UUID.fromString("30000000-0000-0000-0000-000000000002");
  private static final UUID WORKOUT_SESSION_3 = UUID.fromString("30000000-0000-0000-0000-000000000003");
  private static final UUID WORKOUT_SESSION_4 = UUID.fromString("30000000-0000-0000-0000-000000000004");
  private static final UUID WORKOUT_SESSION_5 = UUID.fromString("30000000-0000-0000-0000-000000000005");
  private static final UUID WORKOUT_SESSION_6 = UUID.fromString("30000000-0000-0000-0000-000000000006");
  // Additional sessions for Epic 5 (Performance Report testing)
  private static final UUID WORKOUT_SESSION_7 = UUID.fromString("30000000-0000-0000-0000-000000000007");
  private static final UUID WORKOUT_SESSION_8 = UUID.fromString("30000000-0000-0000-0000-000000000008");
  private static final UUID WORKOUT_SESSION_9 = UUID.fromString("30000000-0000-0000-0000-000000000009");
  private static final UUID WORKOUT_SESSION_10 = UUID.fromString("30000000-0000-0000-0000-000000000010");
  private static final UUID WORKOUT_SESSION_11 = UUID.fromString("30000000-0000-0000-0000-000000000011");
  private static final UUID WORKOUT_SESSION_12 = UUID.fromString("30000000-0000-0000-0000-000000000012");
  // Brazilian members workout sessions (João - 8 sessions, Maria - 6 sessions, Pedro - 10 sessions)
  private static final UUID WORKOUT_SESSION_JOAO_1 = UUID.fromString("30000000-0000-0000-0000-000000000101");
  private static final UUID WORKOUT_SESSION_JOAO_2 = UUID.fromString("30000000-0000-0000-0000-000000000102");
  private static final UUID WORKOUT_SESSION_JOAO_3 = UUID.fromString("30000000-0000-0000-0000-000000000103");
  private static final UUID WORKOUT_SESSION_JOAO_4 = UUID.fromString("30000000-0000-0000-0000-000000000104");
  private static final UUID WORKOUT_SESSION_JOAO_5 = UUID.fromString("30000000-0000-0000-0000-000000000105");
  private static final UUID WORKOUT_SESSION_JOAO_6 = UUID.fromString("30000000-0000-0000-0000-000000000106");
  private static final UUID WORKOUT_SESSION_JOAO_7 = UUID.fromString("30000000-0000-0000-0000-000000000107");
  private static final UUID WORKOUT_SESSION_JOAO_8 = UUID.fromString("30000000-0000-0000-0000-000000000108");
  private static final UUID WORKOUT_SESSION_MARIA_1 = UUID.fromString("30000000-0000-0000-0000-000000000201");
  private static final UUID WORKOUT_SESSION_MARIA_2 = UUID.fromString("30000000-0000-0000-0000-000000000202");
  private static final UUID WORKOUT_SESSION_MARIA_3 = UUID.fromString("30000000-0000-0000-0000-000000000203");
  private static final UUID WORKOUT_SESSION_MARIA_4 = UUID.fromString("30000000-0000-0000-0000-000000000204");
  private static final UUID WORKOUT_SESSION_MARIA_5 = UUID.fromString("30000000-0000-0000-0000-000000000205");
  private static final UUID WORKOUT_SESSION_MARIA_6 = UUID.fromString("30000000-0000-0000-0000-000000000206");
  private static final UUID WORKOUT_SESSION_PEDRO_1 = UUID.fromString("30000000-0000-0000-0000-000000000301");
  private static final UUID WORKOUT_SESSION_PEDRO_2 = UUID.fromString("30000000-0000-0000-0000-000000000302");
  private static final UUID WORKOUT_SESSION_PEDRO_3 = UUID.fromString("30000000-0000-0000-0000-000000000303");
  private static final UUID WORKOUT_SESSION_PEDRO_4 = UUID.fromString("30000000-0000-0000-0000-000000000304");
  private static final UUID WORKOUT_SESSION_PEDRO_5 = UUID.fromString("30000000-0000-0000-0000-000000000305");
  private static final UUID WORKOUT_SESSION_PEDRO_6 = UUID.fromString("30000000-0000-0000-0000-000000000306");
  private static final UUID WORKOUT_SESSION_PEDRO_7 = UUID.fromString("30000000-0000-0000-0000-000000000307");
  private static final UUID WORKOUT_SESSION_PEDRO_8 = UUID.fromString("30000000-0000-0000-0000-000000000308");
  private static final UUID WORKOUT_SESSION_PEDRO_9 = UUID.fromString("30000000-0000-0000-0000-000000000309");
  private static final UUID WORKOUT_SESSION_PEDRO_10 = UUID.fromString("30000000-0000-0000-0000-000000000310");
  
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
      seedWorkoutSessions(conn);
      
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
      System.out.println("  Members (with coach and routines):");
      System.out.println("    Email: alice.member@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println("    Coach: John Coach | Routines: 2");
      System.out.println();
      System.out.println("    Email: bob.member@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println("    Coach: John Coach | Routines: 2");
      System.out.println();
      System.out.println("  Members (unassigned - no coach, no routines):");
      System.out.println("    Email: eva.silva@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println();
      System.out.println("    Email: frank.santos@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println();
      System.out.println("    Email: grace.oliveira@routinerevo.com");
      System.out.println("    Password: member123");
      System.out.println();
      System.out.println("  Membros Brasileiros (gerenciados pelo admin):");
      System.out.println("    Todos usam senha: member123");
      System.out.println();
      System.out.println("    Email: joao.silva@routinerevo.com");
      System.out.println("    Nome: João Silva | Treinos/semana: 4");
      System.out.println();
      System.out.println("    Email: maria.santos@routinerevo.com");
      System.out.println("    Nome: Maria Santos | Treinos/semana: 3");
      System.out.println();
      System.out.println("    Email: pedro.ferreira@routinerevo.com");
      System.out.println("    Nome: Pedro Ferreira | Treinos/semana: 5");
      System.out.println();
      System.out.println("    Email: ana.costa@routinerevo.com");
      System.out.println("    Nome: Ana Costa | Treinos/semana: 4");
      System.out.println();
      System.out.println("    Email: lucas.almeida@routinerevo.com");
      System.out.println("    Nome: Lucas Almeida | Treinos/semana: 3");
      System.out.println();
      System.out.println("    Email: julia.rodrigues@routinerevo.com");
      System.out.println("    Nome: Júlia Rodrigues | Treinos/semana: 4");
      System.out.println();
      System.out.println("    Email: rafael.pereira@routinerevo.com");
      System.out.println("    Nome: Rafael Pereira | Treinos/semana: 5");
      System.out.println();
      System.out.println("    Email: carolina.souza@routinerevo.com");
      System.out.println("    Nome: Carolina Souza | Treinos/semana: 3");
      System.out.println();
      System.out.println("    Email: gustavo.martins@routinerevo.com");
      System.out.println("    Nome: Gustavo Martins | Treinos/semana: 4");
      System.out.println();
      System.out.println("    Email: fernanda.lima@routinerevo.com");
      System.out.println("    Nome: Fernanda Lima | Treinos/semana: 5");
      System.out.println();
      System.out.println("Exercícios:");
      System.out.println("  23 exercícios criados em todos os grupos musculares:");
      System.out.println("    - 3 exercícios de Peito");
      System.out.println("    - 3 exercícios de Costas");
      System.out.println("    - 3 exercícios de Ombro");
      System.out.println("    - 2 exercícios de Bíceps");
      System.out.println("    - 2 exercícios de Tríceps");
      System.out.println("    - 3 exercícios de Perna");
      System.out.println("    - 2 exercícios de Glúteo");
      System.out.println("    - 2 exercícios de Abdômen");
      System.out.println("    - 2 exercícios de Cardio");
      System.out.println("    - 1 exercício Full Body");
      System.out.println();
      System.out.println("Modelos de Rotinas:");
      System.out.println("  6 rotinas TEMPLATE reutilizáveis criadas:");
      System.out.println("    - Força Superior (Template)");
      System.out.println("    - Potência Inferior (Template)");
      System.out.println("    - Circuito Full Body (Template)");
      System.out.println("    - Cardio & Core (Template)");
      System.out.println("    - Treino de Empurrar (Template)");
      System.out.println("    - Treino de Puxar (Template)");
      System.out.println();
      System.out.println("Rotinas Personalizadas:");
      System.out.println("  10 rotinas CUSTOM atribuídas a membros (copiadas de templates):");
      System.out.println("    Alice (John Coach):");
      System.out.println("      - Força Superior [ATIVA]");
      System.out.println("      - Potência Inferior [EXPIRADA]");
      System.out.println("    Bob (John Coach):");
      System.out.println("      - Circuito Full Body [ATIVA]");
      System.out.println("      - Cardio & Core [EXPIRADA]");
      System.out.println("    João (Admin):");
      System.out.println("      - Treino de Superior - João [ATIVA]");
      System.out.println("      - Treino de Inferior - João [ATIVA]");
      System.out.println("    Maria (Admin):");
      System.out.println("      - Circuito Completo - Maria [ATIVA]");
      System.out.println("      - Cardio & Core - Maria [ATIVA]");
      System.out.println("    Pedro (Admin):");
      System.out.println("      - Dia de Empurrar - Pedro [ATIVA]");
      System.out.println("      - Dia de Puxar - Pedro [ATIVA]");
      System.out.println();
      System.out.println("Workout Sessions:");
      System.out.println("  36 workout sessions created for report testing:");
      System.out.println("    - Alice: 7 sessions (Upper Body x4, Lower Body x3)");
      System.out.println("      Adherence Rate: 54.26%");
      System.out.println("    - Bob: 5 sessions (Full Body x3, Cardio & Abs x2)");
      System.out.println("      Adherence Rate: 29.24%");
      System.out.println("    - João: 8 sessions (Upper Body x4, Lower Body x4)");
      System.out.println("      Adherence Rate: 46.78%");
      System.out.println("    - Maria: 6 sessions (Full Body x3, Cardio x3)");
      System.out.println("      Adherence Rate: 46.51%");
      System.out.println("    - Pedro: 10 sessions (Push Day x5, Pull Day x5)");
      System.out.println("      Adherence Rate: 46.73%");
      System.out.println("  Adherence rates updated based on workout history");
      System.out.println();
      System.out.println("Report Testing (GET /api/v1/reports/members/{memberId}):");
      System.out.println("  Members with workout history for reports:");
      System.out.println("    - Alice ID: 00000000-0000-0000-0000-000000000004");
      System.out.println("    - Bob ID:   00000000-0000-0000-0000-000000000005");
      System.out.println("    - João ID:  00000000-0000-0000-0000-000000000011");
      System.out.println("    - Maria ID: 00000000-0000-0000-0000-000000000012");
      System.out.println("    - Pedro ID: 00000000-0000-0000-0000-000000000013");
      System.out.println();
      System.out.println("Profile Endpoints:");
      System.out.println("  GET /api/v1/me - Get Own Profile");
      System.out.println("  PUT /api/v1/me - Update Own Profile (name only)");
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
    
    // Clear workout data first (depends on users, routines, exercises)
    if (tableExists(conn, "workout_items")) {
      String sqlWorkoutItems = "DELETE FROM workout_items";
      try (PreparedStatement stmt = conn.prepareStatement(sqlWorkoutItems)) {
        stmt.executeUpdate();
      }
    }
    
    if (tableExists(conn, "workout_sessions")) {
      String sqlWorkoutSessions = "DELETE FROM workout_sessions";
      try (PreparedStatement stmt = conn.prepareStatement(sqlWorkoutSessions)) {
        stmt.executeUpdate();
      }
    }
    
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
      // Members with workout history use 60-day-old created_at for realistic adherence calculation
      LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);
      
      insertUser(conn, ALICE_MEMBER_ID, "Alice Member", "alice.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", JOHN_COACH_ID, 3, sixtyDaysAgo);
      System.out.println("  ✓ Created member user: alice.member@routinerevo.com");
      
      insertUser(conn, BOB_MEMBER_ID, "Bob Member", "bob.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", JOHN_COACH_ID, 4, sixtyDaysAgo);
      System.out.println("  ✓ Created member user: bob.member@routinerevo.com");
      
      insertUser(conn, CHARLIE_MEMBER_ID, "Charlie Member", "charlie.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "PENDING", SARAH_COACH_ID, 5);
      System.out.println("  ✓ Created pending member user: charlie.member@routinerevo.com");
      
      insertUser(conn, DAVID_MEMBER_ID, "David Member", "david.member@routinerevo.com",
          hashPassword("member123"), "MEMBER", "INACTIVE", SARAH_COACH_ID, 3);
      System.out.println("  ✓ Created inactive member user: david.member@routinerevo.com");
      
      // Unassigned Active Members (no coach, no routines - managed by admin)
      insertUser(conn, EVA_MEMBER_ID, "Eva Silva", "eva.silva@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 4);
      System.out.println("  ✓ Created unassigned member: eva.silva@routinerevo.com");
      
      insertUser(conn, FRANK_MEMBER_ID, "Frank Santos", "frank.santos@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 3);
      System.out.println("  ✓ Created unassigned member: frank.santos@routinerevo.com");
      
      insertUser(conn, GRACE_MEMBER_ID, "Grace Oliveira", "grace.oliveira@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 5);
      System.out.println("  ✓ Created unassigned member: grace.oliveira@routinerevo.com");
      
      // Membros brasileiros (clientes em português, gerenciados diretamente pelo admin)
      // These members have workout history, so use 60-day-old created_at
      insertUser(conn, JOAO_MEMBER_ID, "João Silva", "joao.silva@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 4, sixtyDaysAgo);
      System.out.println("  ✓ Created unassigned member: joao.silva@routinerevo.com");
      
      insertUser(conn, MARIA_MEMBER_ID, "Maria Santos", "maria.santos@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 3, sixtyDaysAgo);
      System.out.println("  ✓ Created unassigned member: maria.santos@routinerevo.com");
      
      insertUser(conn, PEDRO_MEMBER_ID, "Pedro Ferreira", "pedro.ferreira@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 5, sixtyDaysAgo);
      System.out.println("  ✓ Created unassigned member: pedro.ferreira@routinerevo.com");
      
      insertUser(conn, ANA_MEMBER_ID, "Ana Costa", "ana.costa@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 4);
      System.out.println("  ✓ Created unassigned member: ana.costa@routinerevo.com");
      
      insertUser(conn, LUCAS_MEMBER_ID, "Lucas Almeida", "lucas.almeida@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 3);
      System.out.println("  ✓ Created unassigned member: lucas.almeida@routinerevo.com");
      
      insertUser(conn, JULIA_MEMBER_ID, "Júlia Rodrigues", "julia.rodrigues@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 4);
      System.out.println("  ✓ Created unassigned member: julia.rodrigues@routinerevo.com");
      
      insertUser(conn, RAFAEL_MEMBER_ID, "Rafael Pereira", "rafael.pereira@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 5);
      System.out.println("  ✓ Created unassigned member: rafael.pereira@routinerevo.com");
      
      insertUser(conn, CAROLINA_MEMBER_ID, "Carolina Souza", "carolina.souza@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 3);
      System.out.println("  ✓ Created unassigned member: carolina.souza@routinerevo.com");
      
      insertUser(conn, GUSTAVO_MEMBER_ID, "Gustavo Martins", "gustavo.martins@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 4);
      System.out.println("  ✓ Created unassigned member: gustavo.martins@routinerevo.com");
      
      insertUser(conn, FERNANDA_MEMBER_ID, "Fernanda Lima", "fernanda.lima@routinerevo.com",
          hashPassword("member123"), "MEMBER", "ACTIVE", null, 5);
      System.out.println("  ✓ Created unassigned member: fernanda.lima@routinerevo.com");
      
      // Commit transaction
      conn.commit();
      System.out.println("  ✓ Successfully seeded 20 users");
      
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
    // Default: create user "now" (for admins, coaches, users without workout history)
    insertUser(conn, id, name, email, hashedPassword, role, status, coachId, workoutPerWeek, LocalDateTime.now());
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
      Integer workoutPerWeek,
      LocalDateTime createdAt
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
      stmt.setTimestamp(9, Timestamp.valueOf(createdAt));
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
      // Exercise image URLs from Unsplash (free to use)
      // Format: https://images.unsplash.com/photo-{id}?w=400&h=300&fit=crop
      
      // CHEST exercises
      insertExercise(conn, EXERCISE_BENCH_PRESS, "Supino Reto", "CHEST",
          "Exercício composto para peito realizado deitado no banco com barra",
          "Barra, Banco",
          "https://images.unsplash.com/photo-1534368959876-26bf04f2c947?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Supino Reto");
      
      insertExercise(conn, EXERCISE_PUSH_UP, "Flexão de Braço", "CHEST",
          "Exercício de peso corporal para peito, ombros e tríceps",
          "Nenhum (Peso Corporal)",
          "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Flexão de Braço");
      
      insertExercise(conn, EXERCISE_CABLE_FLY, "Crucifixo no Cabo", "CHEST",
          "Exercício de isolamento para peito usando máquina de cabo",
          "Máquina de Cabo",
          "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Crucifixo no Cabo");
      
      // BACK exercises
      insertExercise(conn, EXERCISE_DEADLIFT, "Levantamento Terra", "BACK",
          "Exercício composto que trabalha costas, pernas e core",
          "Barra",
          "https://images.unsplash.com/photo-1517963879433-6ad2b056d712?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Levantamento Terra");
      
      insertExercise(conn, EXERCISE_PULL_UP, "Barra Fixa", "BACK",
          "Exercício de peso corporal para costas e bíceps",
          "Barra de Pullup",
          "https://images.unsplash.com/photo-1598266663439-2056e6900339?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Barra Fixa");
      
      insertExercise(conn, EXERCISE_BARBELL_ROW, "Remada Curvada", "BACK",
          "Exercício composto de remada para espessura das costas",
          "Barra",
          "https://images.unsplash.com/photo-1603287681836-b174ce5074c2?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Remada Curvada");
      
      // SHOULDERS exercises
      insertExercise(conn, EXERCISE_SHOULDER_PRESS, "Desenvolvimento de Ombros", "SHOULDERS",
          "Movimento de pressão acima da cabeça para desenvolvimento dos ombros",
          "Barra ou Halteres",
          "https://images.unsplash.com/photo-1532029837206-abbe2b7620e3?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Desenvolvimento de Ombros");
      
      insertExercise(conn, EXERCISE_LATERAL_RAISE, "Elevação Lateral", "SHOULDERS",
          "Exercício de isolamento para deltoides laterais",
          "Halteres",
          "https://images.unsplash.com/photo-1581009146145-b5ef050c149a?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Elevação Lateral");
      
      insertExercise(conn, EXERCISE_FACE_PULL, "Face Pull", "SHOULDERS",
          "Exercício no cabo trabalhando deltoides posterior e costas superiores",
          "Máquina de Cabo com Corda",
          "https://images.unsplash.com/photo-1534368420009-621bfab424a8?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Face Pull");
      
      // BICEPS exercises
      insertExercise(conn, EXERCISE_BARBELL_CURL, "Rosca Direta", "BICEPS",
          "Exercício clássico de bíceps com barra",
          "Barra",
          "https://images.unsplash.com/photo-1581009137042-c552e485697a?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Rosca Direta");
      
      insertExercise(conn, EXERCISE_HAMMER_CURL, "Rosca Martelo", "BICEPS",
          "Rosca com halteres em pegada neutra para bíceps e antebraços",
          "Halteres",
          "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Rosca Martelo");
      
      // TRICEPS exercises
      insertExercise(conn, EXERCISE_TRICEP_DIPS, "Mergulho de Tríceps", "TRICEPS",
          "Exercício de peso corporal ou com carga para desenvolvimento do tríceps",
          "Barras Paralelas ou Banco",
          "https://images.unsplash.com/photo-1598971457999-ca4ef48a9a71?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Mergulho de Tríceps");
      
      insertExercise(conn, EXERCISE_TRICEP_PUSHDOWN, "Tríceps no Pulley", "TRICEPS",
          "Exercício de isolamento no cabo para tríceps",
          "Máquina de Cabo",
          "https://images.unsplash.com/photo-1597452485669-2c7bb5fef90d?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Tríceps no Pulley");
      
      // LEGS exercises
      insertExercise(conn, EXERCISE_SQUAT, "Agachamento", "LEGS",
          "Rei de todos os exercícios - movimento composto para pernas e core",
          "Barra, Rack de Agachamento",
          "https://images.unsplash.com/photo-1566241142559-40e1dab266c6?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Agachamento");
      
      insertExercise(conn, EXERCISE_LEG_PRESS, "Leg Press", "LEGS",
          "Exercício composto em máquina para quadríceps e glúteos",
          "Máquina de Leg Press",
          "https://images.unsplash.com/photo-1434608519344-49d77a699e1d?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Leg Press");
      
      insertExercise(conn, EXERCISE_LUNGES, "Avanço", "LEGS",
          "Exercício unilateral de pernas para equilíbrio e força",
          "Halteres (opcional)",
          "https://images.unsplash.com/photo-1609899464926-209bc8319a94?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Avanço");
      
      // GLUTES exercises
      insertExercise(conn, EXERCISE_HIP_THRUST, "Elevação de Quadril", "GLUTES",
          "Exercício principal para desenvolvimento e força dos glúteos",
          "Barra, Banco",
          "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Elevação de Quadril");
      
      insertExercise(conn, EXERCISE_GLUTE_BRIDGE, "Ponte de Glúteos", "GLUTES",
          "Exercício de peso corporal ou com carga focando nos glúteos",
          "Nenhum ou Barra",
          "https://images.unsplash.com/photo-1518611012118-696072aa579a?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Ponte de Glúteos");
      
      // ABS exercises
      insertExercise(conn, EXERCISE_PLANK, "Prancha", "ABS",
          "Exercício isométrico para fortalecimento do core",
          "Nenhum (Peso Corporal)",
          "https://images.unsplash.com/photo-1566241142559-40e1dab266c6?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Prancha");
      
      insertExercise(conn, EXERCISE_CRUNCHES, "Abdominal", "ABS",
          "Exercício clássico de abdominais trabalhando o reto abdominal",
          "Nenhum (Peso Corporal)",
          "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Abdominal");
      
      // CARDIO exercises
      insertExercise(conn, EXERCISE_RUNNING, "Corrida", "CARDIO",
          "Exercício cardiovascular para resistência e queima de gordura",
          "Esteira ou Ar Livre",
          "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Corrida");
      
      insertExercise(conn, EXERCISE_CYCLING, "Ciclismo", "CARDIO",
          "Exercício cardio de baixo impacto para resistência",
          "Bicicleta ou Bike Ergométrica",
          "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=400&h=300&fit=crop");
      System.out.println("  ✓ Created exercise: Ciclismo");
      
      // FULL_BODY exercises
      insertExercise(conn, EXERCISE_BURPEES, "Burpees", "FULL_BODY",
          "Exercício de alta intensidade combinando agachamento, flexão e salto",
          "Nenhum (Peso Corporal)",
          "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?w=400&h=300&fit=crop");
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
      // Calculate expiration dates
      LocalDateTime expirationDateActive = LocalDateTime.now().plusDays(30);  // Active routines
      LocalDateTime expirationDateExpired = LocalDateTime.now().minusDays(10); // Expired routines
      
      // ==========================================
      // ROUTINE TEMPLATES (reusable, no member assigned)
      // ==========================================
      System.out.println("  Creating routine templates...");
      
      // TEMPLATE 1: Força Superior
      insertRoutine(conn, TEMPLATE_UPPER_BODY, "Força Superior",
          "Treino completo de membros superiores focando em peito, costas e ombros",
          null, JOHN_COACH_ID, null, "TEMPLATE", null);
      System.out.println("  ✓ Created template: Força Superior");
      
      // Add items to Upper Body Template
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_UPPER_BODY, EXERCISE_BENCH_PRESS,
          4, "8-10", 60.0, "90 seg", 1);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_UPPER_BODY, EXERCISE_BARBELL_ROW,
          4, "8-10", 55.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_UPPER_BODY, EXERCISE_SHOULDER_PRESS,
          3, "10-12", 40.0, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_UPPER_BODY, EXERCISE_LATERAL_RAISE,
          3, "12-15", 10.0, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_UPPER_BODY, EXERCISE_BARBELL_CURL,
          3, "10-12", 25.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_UPPER_BODY, EXERCISE_TRICEP_PUSHDOWN,
          3, "12-15", null, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises to template");
      
      // TEMPLATE 2: Potência Inferior
      insertRoutine(conn, TEMPLATE_LOWER_BODY, "Potência Inferior",
          "Dia de pernas focando em força e desenvolvimento muscular",
          null, JOHN_COACH_ID, null, "TEMPLATE", null);
      System.out.println("  ✓ Created template: Potência Inferior");
      
      // Add items to Lower Body Template
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_LOWER_BODY, EXERCISE_SQUAT,
          5, "5-8", 80.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_LOWER_BODY, EXERCISE_LEG_PRESS,
          4, "10-12", 120.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_LOWER_BODY, EXERCISE_LUNGES,
          3, "12-15/perna", 15.0, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_LOWER_BODY, EXERCISE_HIP_THRUST,
          4, "10-12", 70.0, "90 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_LOWER_BODY, EXERCISE_GLUTE_BRIDGE,
          3, "15-20", 40.0, "45 seg", 5);
      System.out.println("    ✓ Added 5 exercises to template");
      
      // TEMPLATE 3: Circuito Full Body
      insertRoutine(conn, TEMPLATE_FULL_BODY, "Circuito Full Body",
          "Treino completo de corpo inteiro para condicionamento geral",
          null, JOHN_COACH_ID, null, "TEMPLATE", null);
      System.out.println("  ✓ Created template: Circuito Full Body");
      
      // Add items to Full Body Template
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_FULL_BODY, EXERCISE_DEADLIFT,
          4, "6-8", 90.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_FULL_BODY, EXERCISE_BENCH_PRESS,
          3, "8-10", 55.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_FULL_BODY, EXERCISE_SQUAT,
          4, "8-10", 70.0, "2 min", 3);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_FULL_BODY, EXERCISE_PULL_UP,
          3, "AMRAP", null, "90 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_FULL_BODY, EXERCISE_SHOULDER_PRESS,
          3, "10-12", 35.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_FULL_BODY, EXERCISE_PLANK,
          3, "45-60 seg", null, "60 seg", 6);
      System.out.println("    ✓ Added 6 exercises to template");
      
      // TEMPLATE 4: Cardio & Core
      insertRoutine(conn, TEMPLATE_CARDIO_ABS, "Cardio & Core",
          "Rotina de alta intensidade para cardio e fortalecimento do core",
          null, JOHN_COACH_ID, null, "TEMPLATE", null);
      System.out.println("  ✓ Created template: Cardio & Core");
      
      // Add items to Cardio & Abs Template
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_CARDIO_ABS, EXERCISE_RUNNING,
          1, "20 min", null, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_CARDIO_ABS, EXERCISE_BURPEES,
          3, "15-20", null, "60 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_CARDIO_ABS, EXERCISE_PLANK,
          4, "45-60 seg", null, "45 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_CARDIO_ABS, EXERCISE_CRUNCHES,
          4, "20-25", null, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_CARDIO_ABS, EXERCISE_CYCLING,
          1, "15 min", null, "fim", 5);
      System.out.println("    ✓ Added 5 exercises to template");
      
      // TEMPLATE 5: Treino de Empurrar
      insertRoutine(conn, TEMPLATE_PUSH_DAY, "Treino de Empurrar",
          "Rotina focada em peito, ombros e tríceps",
          null, SARAH_COACH_ID, null, "TEMPLATE", null);
      System.out.println("  ✓ Created template: Treino de Empurrar");
      
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PUSH_DAY, EXERCISE_BENCH_PRESS,
          4, "8-10", 60.0, "90 seg", 1);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PUSH_DAY, EXERCISE_SHOULDER_PRESS,
          4, "8-10", 40.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PUSH_DAY, EXERCISE_CABLE_FLY,
          3, "12-15", null, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PUSH_DAY, EXERCISE_LATERAL_RAISE,
          3, "15-20", 8.0, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PUSH_DAY, EXERCISE_TRICEP_DIPS,
          3, "10-12", null, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PUSH_DAY, EXERCISE_TRICEP_PUSHDOWN,
          3, "12-15", null, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises to template");
      
      // TEMPLATE 6: Treino de Puxar
      insertRoutine(conn, TEMPLATE_PULL_DAY, "Treino de Puxar",
          "Rotina focada em costas e bíceps",
          null, SARAH_COACH_ID, null, "TEMPLATE", null);
      System.out.println("  ✓ Created template: Treino de Puxar");
      
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PULL_DAY, EXERCISE_DEADLIFT,
          4, "5-6", 100.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PULL_DAY, EXERCISE_PULL_UP,
          4, "AMRAP", null, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PULL_DAY, EXERCISE_BARBELL_ROW,
          4, "8-10", 55.0, "90 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PULL_DAY, EXERCISE_FACE_PULL,
          3, "15-20", null, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PULL_DAY, EXERCISE_BARBELL_CURL,
          3, "10-12", 25.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), TEMPLATE_PULL_DAY, EXERCISE_HAMMER_CURL,
          3, "12-15", 12.5, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises to template");
      
      // ==========================================
      // CUSTOM ROUTINES (assigned to specific members, copied from templates)
      // ==========================================
      System.out.println("  Creating custom routines for members...");
      
      // CUSTOM 1: Alice's Upper Body (from Upper Body template) - ACTIVE
      insertRoutine(conn, ROUTINE_ALICE_UPPER, "Força Superior",
          "Treino completo de membros superiores focando em peito, costas e ombros",
          expirationDateActive, JOHN_COACH_ID, ALICE_MEMBER_ID, "CUSTOM", TEMPLATE_UPPER_BODY);
      System.out.println("  ✓ Created custom routine: Alice - Força Superior");
      
      // Add items (copy from template)
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_UPPER, EXERCISE_BENCH_PRESS,
          4, "8-10", 60.0, "90 seg", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_UPPER, EXERCISE_BARBELL_ROW,
          4, "8-10", 55.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_UPPER, EXERCISE_SHOULDER_PRESS,
          3, "10-12", 40.0, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_UPPER, EXERCISE_LATERAL_RAISE,
          3, "12-15", 10.0, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_UPPER, EXERCISE_BARBELL_CURL,
          3, "10-12", 25.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_UPPER, EXERCISE_TRICEP_PUSHDOWN,
          3, "12-15", null, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises");
      
      // CUSTOM 2: Alice's Lower Body (from Lower Body template) - EXPIRED
      insertRoutine(conn, ROUTINE_ALICE_LOWER, "Potência Inferior",
          "Dia de pernas focando em força e desenvolvimento muscular",
          expirationDateExpired, JOHN_COACH_ID, ALICE_MEMBER_ID, "CUSTOM", TEMPLATE_LOWER_BODY);
      System.out.println("  ✓ Created custom routine: Alice - Potência Inferior");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_LOWER, EXERCISE_SQUAT,
          5, "5-8", 80.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_LOWER, EXERCISE_LEG_PRESS,
          4, "10-12", 120.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_LOWER, EXERCISE_LUNGES,
          3, "12-15/perna", 15.0, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_LOWER, EXERCISE_HIP_THRUST,
          4, "10-12", 70.0, "90 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_ALICE_LOWER, EXERCISE_GLUTE_BRIDGE,
          3, "15-20", 40.0, "45 seg", 5);
      System.out.println("    ✓ Added 5 exercises");
      
      // CUSTOM 3: Bob's Full Body (from Full Body template) - ACTIVE
      insertRoutine(conn, ROUTINE_BOB_FULL, "Circuito Full Body",
          "Treino completo de corpo inteiro para condicionamento geral",
          expirationDateActive, JOHN_COACH_ID, BOB_MEMBER_ID, "CUSTOM", TEMPLATE_FULL_BODY);
      System.out.println("  ✓ Created custom routine: Bob - Circuito Full Body");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_FULL, EXERCISE_DEADLIFT,
          4, "6-8", 90.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_FULL, EXERCISE_BENCH_PRESS,
          3, "8-10", 55.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_FULL, EXERCISE_SQUAT,
          4, "8-10", 70.0, "2 min", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_FULL, EXERCISE_PULL_UP,
          3, "AMRAP", null, "90 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_FULL, EXERCISE_SHOULDER_PRESS,
          3, "10-12", 35.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_FULL, EXERCISE_PLANK,
          3, "45-60 seg", null, "60 seg", 6);
      System.out.println("    ✓ Added 6 exercises");
      
      // CUSTOM 4: Bob's Cardio & Abs (from Cardio template) - EXPIRED
      insertRoutine(conn, ROUTINE_BOB_CARDIO, "Cardio & Core",
          "Rotina de alta intensidade para cardio e fortalecimento do core",
          expirationDateExpired, JOHN_COACH_ID, BOB_MEMBER_ID, "CUSTOM", TEMPLATE_CARDIO_ABS);
      System.out.println("  ✓ Created custom routine: Bob - Cardio & Core");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_CARDIO, EXERCISE_RUNNING,
          1, "20 min", null, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_CARDIO, EXERCISE_BURPEES,
          3, "15-20", null, "60 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_CARDIO, EXERCISE_PLANK,
          4, "45-60 seg", null, "45 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_CARDIO, EXERCISE_CRUNCHES,
          4, "20-25", null, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_BOB_CARDIO, EXERCISE_CYCLING,
          1, "15 min", null, "fim", 5);
      System.out.println("    ✓ Added 5 exercises");
      
      // ==========================================
      // BRAZILIAN MEMBERS ROUTINES (for report testing)
      // ==========================================
      System.out.println("  Creating routines for Brazilian members...");
      
      // CUSTOM 5: João's Upper Body (from Upper Body template) - ACTIVE
      insertRoutine(conn, ROUTINE_JOAO_UPPER, "Treino de Superior - João",
          "Treino personalizado de membros superiores para João",
          expirationDateActive, ADMIN_ID, JOAO_MEMBER_ID, "CUSTOM", TEMPLATE_UPPER_BODY);
      System.out.println("  ✓ Created custom routine: João - Treino Superior");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_UPPER, EXERCISE_BENCH_PRESS,
          4, "10-12", 50.0, "90 seg", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_UPPER, EXERCISE_BARBELL_ROW,
          4, "10-12", 45.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_UPPER, EXERCISE_SHOULDER_PRESS,
          3, "12-15", 30.0, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_UPPER, EXERCISE_LATERAL_RAISE,
          3, "15-20", 8.0, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_UPPER, EXERCISE_BARBELL_CURL,
          3, "12-15", 20.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_UPPER, EXERCISE_TRICEP_PUSHDOWN,
          3, "15-20", null, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises");
      
      // CUSTOM 6: João's Lower Body (from Lower Body template) - ACTIVE
      insertRoutine(conn, ROUTINE_JOAO_LOWER, "Treino de Inferior - João",
          "Treino personalizado de membros inferiores para João",
          expirationDateActive, ADMIN_ID, JOAO_MEMBER_ID, "CUSTOM", TEMPLATE_LOWER_BODY);
      System.out.println("  ✓ Created custom routine: João - Treino Inferior");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_LOWER, EXERCISE_SQUAT,
          4, "8-10", 70.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_LOWER, EXERCISE_LEG_PRESS,
          4, "12-15", 100.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_LOWER, EXERCISE_LUNGES,
          3, "12/perna", 12.0, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_JOAO_LOWER, EXERCISE_HIP_THRUST,
          4, "12-15", 60.0, "90 seg", 4);
      System.out.println("    ✓ Added 4 exercises");
      
      // CUSTOM 7: Maria's Full Body (from Full Body template) - ACTIVE
      insertRoutine(conn, ROUTINE_MARIA_FULL, "Circuito Completo - Maria",
          "Treino de corpo inteiro para condicionamento geral",
          expirationDateActive, ADMIN_ID, MARIA_MEMBER_ID, "CUSTOM", TEMPLATE_FULL_BODY);
      System.out.println("  ✓ Created custom routine: Maria - Circuito Completo");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_FULL, EXERCISE_DEADLIFT,
          3, "8-10", 60.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_FULL, EXERCISE_BENCH_PRESS,
          3, "10-12", 35.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_FULL, EXERCISE_SQUAT,
          3, "10-12", 50.0, "2 min", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_FULL, EXERCISE_PULL_UP,
          3, "6-8", null, "90 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_FULL, EXERCISE_PLANK,
          3, "30-45 seg", null, "60 seg", 5);
      System.out.println("    ✓ Added 5 exercises");
      
      // CUSTOM 8: Maria's Cardio (from Cardio template) - ACTIVE
      insertRoutine(conn, ROUTINE_MARIA_CARDIO, "Cardio & Core - Maria",
          "Rotina de cardio e fortalecimento do core",
          expirationDateActive, ADMIN_ID, MARIA_MEMBER_ID, "CUSTOM", TEMPLATE_CARDIO_ABS);
      System.out.println("  ✓ Created custom routine: Maria - Cardio & Core");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_CARDIO, EXERCISE_RUNNING,
          1, "15 min", null, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_CARDIO, EXERCISE_BURPEES,
          3, "12-15", null, "60 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_CARDIO, EXERCISE_PLANK,
          3, "30-45 seg", null, "45 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_MARIA_CARDIO, EXERCISE_CRUNCHES,
          3, "15-20", null, "45 seg", 4);
      System.out.println("    ✓ Added 4 exercises");
      
      // CUSTOM 9: Pedro's Push Day (from Push template) - ACTIVE
      insertRoutine(conn, ROUTINE_PEDRO_PUSH, "Dia de Empurrar - Pedro",
          "Treino focado em peito, ombros e tríceps",
          expirationDateActive, ADMIN_ID, PEDRO_MEMBER_ID, "CUSTOM", TEMPLATE_PUSH_DAY);
      System.out.println("  ✓ Created custom routine: Pedro - Dia de Empurrar");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PUSH, EXERCISE_BENCH_PRESS,
          5, "6-8", 80.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PUSH, EXERCISE_SHOULDER_PRESS,
          4, "8-10", 50.0, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PUSH, EXERCISE_CABLE_FLY,
          3, "12-15", null, "60 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PUSH, EXERCISE_LATERAL_RAISE,
          4, "12-15", 12.0, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PUSH, EXERCISE_TRICEP_DIPS,
          4, "8-10", null, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PUSH, EXERCISE_TRICEP_PUSHDOWN,
          3, "12-15", null, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises");
      
      // CUSTOM 10: Pedro's Pull Day (from Pull template) - ACTIVE
      insertRoutine(conn, ROUTINE_PEDRO_PULL, "Dia de Puxar - Pedro",
          "Treino focado em costas e bíceps",
          expirationDateActive, ADMIN_ID, PEDRO_MEMBER_ID, "CUSTOM", TEMPLATE_PULL_DAY);
      System.out.println("  ✓ Created custom routine: Pedro - Dia de Puxar");
      
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PULL, EXERCISE_DEADLIFT,
          5, "5-6", 120.0, "2 min", 1);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PULL, EXERCISE_PULL_UP,
          4, "AMRAP", null, "90 seg", 2);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PULL, EXERCISE_BARBELL_ROW,
          4, "8-10", 70.0, "90 seg", 3);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PULL, EXERCISE_FACE_PULL,
          3, "15-20", null, "45 seg", 4);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PULL, EXERCISE_BARBELL_CURL,
          4, "10-12", 30.0, "60 seg", 5);
      insertRoutineItem(conn, UUID.randomUUID(), ROUTINE_PEDRO_PULL, EXERCISE_HAMMER_CURL,
          3, "12-15", 15.0, "45 seg", 6);
      System.out.println("    ✓ Added 6 exercises");
      
      // Commit transaction
      conn.commit();
      System.out.println("  ✓ Successfully seeded 6 templates + 10 custom routines with 87 total exercises");
      
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
      UUID memberId,
      String routineType,
      UUID templateId
  ) throws SQLException {
    String sql = "INSERT INTO routines (id, name, description, expiration_date, creator_id, member_id, routine_type, template_id, created_at, updated_at) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    LocalDateTime now = LocalDateTime.now();
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setString(2, name);
      stmt.setString(3, description);
      stmt.setTimestamp(4, expirationDate != null ? Timestamp.valueOf(expirationDate) : null);
      stmt.setObject(5, creatorId);
      stmt.setObject(6, memberId);
      stmt.setString(7, routineType);
      stmt.setObject(8, templateId);
      stmt.setTimestamp(9, Timestamp.valueOf(now));
      stmt.setTimestamp(10, Timestamp.valueOf(now));
      
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
  
  private static void seedWorkoutSessions(Connection conn) throws SQLException {
    if (!tableExists(conn, "workout_sessions")) {
      System.out.println("⚠ Workout sessions table does not exist yet. Skipping workout seeding.");
      System.out.println("  Run Flyway migration V7 first to create the workout tables.");
      return;
    }
    
    System.out.println("Seeding workout sessions...");
    
    // Disable auto-commit for transaction
    conn.setAutoCommit(false);
    
    try {
      // Create workout sessions spread over the last 30 days
      LocalDateTime now = LocalDateTime.now();
      
      // ==========================================
      // ALICE's Workout Sessions (4 sessions)
      // ==========================================
      
      // Session 1: Upper Body - 3 days ago
      LocalDateTime session1Start = now.minusDays(3).withHour(9).withMinute(0);
      LocalDateTime session1End = session1Start.plusHours(1);
      insertWorkoutSession(conn, WORKOUT_SESSION_1, ALICE_MEMBER_ID, ROUTINE_ALICE_UPPER,
          session1Start, session1End);
      System.out.println("  ✓ Created workout session: Alice - Upper Body (3 days ago)");
      
      // Add workout items for Session 1
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_1, EXERCISE_BENCH_PRESS, 4, 10, 60.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_1, EXERCISE_BARBELL_ROW, 4, 10, 55.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_1, EXERCISE_SHOULDER_PRESS, 3, 12, 40.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_1, EXERCISE_BARBELL_CURL, 3, 12, 25.0);
      System.out.println("    ✓ Added 4 exercises to session");
      
      // Session 2: Lower Body - 5 days ago
      LocalDateTime session2Start = now.minusDays(5).withHour(17).withMinute(30);
      LocalDateTime session2End = session2Start.plusMinutes(75);
      insertWorkoutSession(conn, WORKOUT_SESSION_2, ALICE_MEMBER_ID, ROUTINE_ALICE_LOWER,
          session2Start, session2End);
      System.out.println("  ✓ Created workout session: Alice - Lower Body (5 days ago)");
      
      // Add workout items for Session 2
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_2, EXERCISE_SQUAT, 5, 8, 80.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_2, EXERCISE_LEG_PRESS, 4, 12, 120.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_2, EXERCISE_LUNGES, 3, 15, 15.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_2, EXERCISE_HIP_THRUST, 4, 12, 70.0);
      System.out.println("    ✓ Added 4 exercises to session");
      
      // Session 3: Upper Body - 10 days ago
      LocalDateTime session3Start = now.minusDays(10).withHour(8).withMinute(0);
      LocalDateTime session3End = session3Start.plusMinutes(55);
      insertWorkoutSession(conn, WORKOUT_SESSION_3, ALICE_MEMBER_ID, ROUTINE_ALICE_UPPER,
          session3Start, session3End);
      System.out.println("  ✓ Created workout session: Alice - Upper Body (10 days ago)");
      
      // Add workout items for Session 3
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_3, EXERCISE_BENCH_PRESS, 4, 8, 62.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_3, EXERCISE_PULL_UP, 3, 8, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_3, EXERCISE_LATERAL_RAISE, 3, 15, 10.0);
      System.out.println("    ✓ Added 3 exercises to session");
      
      // Session 4: Lower Body - 14 days ago
      LocalDateTime session4Start = now.minusDays(14).withHour(18).withMinute(0);
      LocalDateTime session4End = session4Start.plusMinutes(80);
      insertWorkoutSession(conn, WORKOUT_SESSION_4, ALICE_MEMBER_ID, ROUTINE_ALICE_LOWER,
          session4Start, session4End);
      System.out.println("  ✓ Created workout session: Alice - Lower Body (14 days ago)");
      
      // Add workout items for Session 4
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_4, EXERCISE_SQUAT, 5, 6, 85.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_4, EXERCISE_DEADLIFT, 4, 6, 90.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_4, EXERCISE_GLUTE_BRIDGE, 3, 20, 40.0);
      System.out.println("    ✓ Added 3 exercises to session");
      
      // ==========================================
      // BOB's Workout Sessions (2 sessions)
      // ==========================================
      
      // Session 5: Full Body - 7 days ago
      LocalDateTime session5Start = now.minusDays(7).withHour(7).withMinute(0);
      LocalDateTime session5End = session5Start.plusMinutes(90);
      insertWorkoutSession(conn, WORKOUT_SESSION_5, BOB_MEMBER_ID, ROUTINE_BOB_FULL,
          session5Start, session5End);
      System.out.println("  ✓ Created workout session: Bob - Full Body (7 days ago)");
      
      // Add workout items for Session 5
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_5, EXERCISE_DEADLIFT, 4, 8, 90.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_5, EXERCISE_BENCH_PRESS, 3, 10, 55.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_5, EXERCISE_SQUAT, 4, 10, 70.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_5, EXERCISE_PULL_UP, 3, 6, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_5, EXERCISE_PLANK, 3, 60, null);
      System.out.println("    ✓ Added 5 exercises to session");
      
      // Session 6: Cardio & Abs - 2 days ago
      LocalDateTime session6Start = now.minusDays(2).withHour(6).withMinute(30);
      LocalDateTime session6End = session6Start.plusMinutes(45);
      insertWorkoutSession(conn, WORKOUT_SESSION_6, BOB_MEMBER_ID, ROUTINE_BOB_CARDIO,
          session6Start, session6End);
      System.out.println("  ✓ Created workout session: Bob - Cardio & Abs (2 days ago)");
      
      // Add workout items for Session 6
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_6, EXERCISE_RUNNING, 1, 20, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_6, EXERCISE_BURPEES, 3, 15, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_6, EXERCISE_PLANK, 4, 45, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_6, EXERCISE_CRUNCHES, 4, 25, null);
      System.out.println("    ✓ Added 4 exercises to session");
      
      // ==========================================
      // Additional ALICE's Workout Sessions for Epic 5 Report Testing
      // ==========================================
      
      // Session 7: Upper Body - 17 days ago
      LocalDateTime session7Start = now.minusDays(17).withHour(10).withMinute(0);
      LocalDateTime session7End = session7Start.plusMinutes(65);
      insertWorkoutSession(conn, WORKOUT_SESSION_7, ALICE_MEMBER_ID, ROUTINE_ALICE_UPPER,
          session7Start, session7End);
      System.out.println("  ✓ Created workout session: Alice - Upper Body (17 days ago)");
      
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_7, EXERCISE_BENCH_PRESS, 4, 9, 57.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_7, EXERCISE_BARBELL_ROW, 4, 10, 52.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_7, EXERCISE_FACE_PULL, 3, 15, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_7, EXERCISE_TRICEP_DIPS, 3, 12, null);
      System.out.println("    ✓ Added 4 exercises to session");
      
      // Session 8: Lower Body - 21 days ago
      LocalDateTime session8Start = now.minusDays(21).withHour(16).withMinute(0);
      LocalDateTime session8End = session8Start.plusMinutes(70);
      insertWorkoutSession(conn, WORKOUT_SESSION_8, ALICE_MEMBER_ID, ROUTINE_ALICE_LOWER,
          session8Start, session8End);
      System.out.println("  ✓ Created workout session: Alice - Lower Body (21 days ago)");
      
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_8, EXERCISE_SQUAT, 5, 7, 77.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_8, EXERCISE_LEG_PRESS, 4, 11, 115.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_8, EXERCISE_LUNGES, 3, 14, 12.5);
      System.out.println("    ✓ Added 3 exercises to session");
      
      // Session 9: Upper Body - 25 days ago
      LocalDateTime session9Start = now.minusDays(25).withHour(9).withMinute(30);
      LocalDateTime session9End = session9Start.plusMinutes(58);
      insertWorkoutSession(conn, WORKOUT_SESSION_9, ALICE_MEMBER_ID, ROUTINE_ALICE_UPPER,
          session9Start, session9End);
      System.out.println("  ✓ Created workout session: Alice - Upper Body (25 days ago)");
      
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_9, EXERCISE_BENCH_PRESS, 4, 8, 55.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_9, EXERCISE_CABLE_FLY, 3, 12, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_9, EXERCISE_HAMMER_CURL, 3, 12, 12.5);
      System.out.println("    ✓ Added 3 exercises to session");
      
      // ==========================================
      // Additional BOB's Workout Sessions for Epic 5 Report Testing
      // ==========================================
      
      // Session 10: Full Body - 12 days ago
      LocalDateTime session10Start = now.minusDays(12).withHour(6).withMinute(0);
      LocalDateTime session10End = session10Start.plusMinutes(85);
      insertWorkoutSession(conn, WORKOUT_SESSION_10, BOB_MEMBER_ID, ROUTINE_BOB_FULL,
          session10Start, session10End);
      System.out.println("  ✓ Created workout session: Bob - Full Body (12 days ago)");
      
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_10, EXERCISE_DEADLIFT, 4, 7, 87.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_10, EXERCISE_SQUAT, 4, 9, 67.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_10, EXERCISE_SHOULDER_PRESS, 3, 11, 32.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_10, EXERCISE_PLANK, 3, 50, null);
      System.out.println("    ✓ Added 4 exercises to session");
      
      // Session 11: Cardio & Abs - 18 days ago
      LocalDateTime session11Start = now.minusDays(18).withHour(7).withMinute(0);
      LocalDateTime session11End = session11Start.plusMinutes(40);
      insertWorkoutSession(conn, WORKOUT_SESSION_11, BOB_MEMBER_ID, ROUTINE_BOB_CARDIO,
          session11Start, session11End);
      System.out.println("  ✓ Created workout session: Bob - Cardio & Abs (18 days ago)");
      
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_11, EXERCISE_CYCLING, 1, 15, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_11, EXERCISE_BURPEES, 3, 12, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_11, EXERCISE_CRUNCHES, 4, 20, null);
      System.out.println("    ✓ Added 3 exercises to session");
      
      // Session 12: Full Body - 24 days ago
      LocalDateTime session12Start = now.minusDays(24).withHour(17).withMinute(30);
      LocalDateTime session12End = session12Start.plusMinutes(95);
      insertWorkoutSession(conn, WORKOUT_SESSION_12, BOB_MEMBER_ID, ROUTINE_BOB_FULL,
          session12Start, session12End);
      System.out.println("  ✓ Created workout session: Bob - Full Body (24 days ago)");
      
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_12, EXERCISE_DEADLIFT, 4, 6, 85.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_12, EXERCISE_BENCH_PRESS, 3, 9, 52.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_12, EXERCISE_SQUAT, 4, 8, 65.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_12, EXERCISE_PULL_UP, 3, 5, null);
      System.out.println("    ✓ Added 4 exercises to session");
      
      // ==========================================
      // JOÃO's Workout Sessions (8 sessions - high adherence member)
      // Target: 4/week, 30 days = ~17.1 expected, 8 done = 46.8% adherence
      // ==========================================
      System.out.println("  Creating João's workout sessions...");
      
      // João Session 1: Upper Body - 2 days ago
      LocalDateTime joaoSession1Start = now.minusDays(2).withHour(6).withMinute(30);
      LocalDateTime joaoSession1End = joaoSession1Start.plusMinutes(65);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_1, JOAO_MEMBER_ID, ROUTINE_JOAO_UPPER,
          joaoSession1Start, joaoSession1End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_1, EXERCISE_BENCH_PRESS, 4, 11, 52.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_1, EXERCISE_BARBELL_ROW, 4, 11, 47.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_1, EXERCISE_SHOULDER_PRESS, 3, 13, 32.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_1, EXERCISE_BARBELL_CURL, 3, 14, 22.5);
      System.out.println("    ✓ João - Upper Body (2 days ago)");
      
      // João Session 2: Lower Body - 4 days ago
      LocalDateTime joaoSession2Start = now.minusDays(4).withHour(18).withMinute(0);
      LocalDateTime joaoSession2End = joaoSession2Start.plusMinutes(70);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_2, JOAO_MEMBER_ID, ROUTINE_JOAO_LOWER,
          joaoSession2Start, joaoSession2End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_2, EXERCISE_SQUAT, 4, 9, 72.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_2, EXERCISE_LEG_PRESS, 4, 13, 105.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_2, EXERCISE_LUNGES, 3, 12, 14.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_2, EXERCISE_HIP_THRUST, 4, 13, 62.5);
      System.out.println("    ✓ João - Lower Body (4 days ago)");
      
      // João Session 3: Upper Body - 6 days ago
      LocalDateTime joaoSession3Start = now.minusDays(6).withHour(7).withMinute(0);
      LocalDateTime joaoSession3End = joaoSession3Start.plusMinutes(60);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_3, JOAO_MEMBER_ID, ROUTINE_JOAO_UPPER,
          joaoSession3Start, joaoSession3End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_3, EXERCISE_BENCH_PRESS, 4, 10, 50.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_3, EXERCISE_SHOULDER_PRESS, 3, 12, 30.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_3, EXERCISE_TRICEP_PUSHDOWN, 3, 16, null);
      System.out.println("    ✓ João - Upper Body (6 days ago)");
      
      // João Session 4: Lower Body - 9 days ago
      LocalDateTime joaoSession4Start = now.minusDays(9).withHour(17).withMinute(30);
      LocalDateTime joaoSession4End = joaoSession4Start.plusMinutes(75);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_4, JOAO_MEMBER_ID, ROUTINE_JOAO_LOWER,
          joaoSession4Start, joaoSession4End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_4, EXERCISE_SQUAT, 4, 8, 70.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_4, EXERCISE_LEG_PRESS, 4, 12, 100.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_4, EXERCISE_HIP_THRUST, 4, 12, 60.0);
      System.out.println("    ✓ João - Lower Body (9 days ago)");
      
      // João Session 5: Upper Body - 13 days ago
      LocalDateTime joaoSession5Start = now.minusDays(13).withHour(6).withMinute(0);
      LocalDateTime joaoSession5End = joaoSession5Start.plusMinutes(68);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_5, JOAO_MEMBER_ID, ROUTINE_JOAO_UPPER,
          joaoSession5Start, joaoSession5End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_5, EXERCISE_BENCH_PRESS, 4, 10, 47.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_5, EXERCISE_BARBELL_ROW, 4, 10, 45.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_5, EXERCISE_LATERAL_RAISE, 3, 17, 8.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_5, EXERCISE_BARBELL_CURL, 3, 13, 20.0);
      System.out.println("    ✓ João - Upper Body (13 days ago)");
      
      // João Session 6: Lower Body - 16 days ago
      LocalDateTime joaoSession6Start = now.minusDays(16).withHour(18).withMinute(30);
      LocalDateTime joaoSession6End = joaoSession6Start.plusMinutes(72);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_6, JOAO_MEMBER_ID, ROUTINE_JOAO_LOWER,
          joaoSession6Start, joaoSession6End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_6, EXERCISE_SQUAT, 4, 9, 67.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_6, EXERCISE_LEG_PRESS, 4, 14, 95.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_6, EXERCISE_LUNGES, 3, 11, 12.0);
      System.out.println("    ✓ João - Lower Body (16 days ago)");
      
      // João Session 7: Upper Body - 20 days ago
      LocalDateTime joaoSession7Start = now.minusDays(20).withHour(7).withMinute(0);
      LocalDateTime joaoSession7End = joaoSession7Start.plusMinutes(62);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_7, JOAO_MEMBER_ID, ROUTINE_JOAO_UPPER,
          joaoSession7Start, joaoSession7End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_7, EXERCISE_BENCH_PRESS, 4, 9, 45.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_7, EXERCISE_BARBELL_ROW, 4, 10, 42.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_7, EXERCISE_SHOULDER_PRESS, 3, 11, 27.5);
      System.out.println("    ✓ João - Upper Body (20 days ago)");
      
      // João Session 8: Lower Body - 23 days ago
      LocalDateTime joaoSession8Start = now.minusDays(23).withHour(17).withMinute(0);
      LocalDateTime joaoSession8End = joaoSession8Start.plusMinutes(70);
      insertWorkoutSession(conn, WORKOUT_SESSION_JOAO_8, JOAO_MEMBER_ID, ROUTINE_JOAO_LOWER,
          joaoSession8Start, joaoSession8End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_8, EXERCISE_SQUAT, 4, 8, 65.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_8, EXERCISE_LEG_PRESS, 4, 12, 90.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_JOAO_8, EXERCISE_HIP_THRUST, 4, 11, 55.0);
      System.out.println("    ✓ João - Lower Body (23 days ago)");
      
      // ==========================================
      // MARIA's Workout Sessions (6 sessions - moderate adherence)
      // Target: 3/week, 30 days = ~12.9 expected, 6 done = 46.5% adherence
      // ==========================================
      System.out.println("  Creating Maria's workout sessions...");
      
      // Maria Session 1: Full Body - 3 days ago
      LocalDateTime mariaSession1Start = now.minusDays(3).withHour(8).withMinute(0);
      LocalDateTime mariaSession1End = mariaSession1Start.plusMinutes(55);
      insertWorkoutSession(conn, WORKOUT_SESSION_MARIA_1, MARIA_MEMBER_ID, ROUTINE_MARIA_FULL,
          mariaSession1Start, mariaSession1End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_1, EXERCISE_DEADLIFT, 3, 9, 62.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_1, EXERCISE_BENCH_PRESS, 3, 11, 37.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_1, EXERCISE_SQUAT, 3, 11, 52.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_1, EXERCISE_PLANK, 3, 40, null);
      System.out.println("    ✓ Maria - Full Body (3 days ago)");
      
      // Maria Session 2: Cardio - 6 days ago
      LocalDateTime mariaSession2Start = now.minusDays(6).withHour(6).withMinute(30);
      LocalDateTime mariaSession2End = mariaSession2Start.plusMinutes(40);
      insertWorkoutSession(conn, WORKOUT_SESSION_MARIA_2, MARIA_MEMBER_ID, ROUTINE_MARIA_CARDIO,
          mariaSession2Start, mariaSession2End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_2, EXERCISE_RUNNING, 1, 15, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_2, EXERCISE_BURPEES, 3, 13, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_2, EXERCISE_CRUNCHES, 3, 18, null);
      System.out.println("    ✓ Maria - Cardio (6 days ago)");
      
      // Maria Session 3: Full Body - 10 days ago
      LocalDateTime mariaSession3Start = now.minusDays(10).withHour(7).withMinute(30);
      LocalDateTime mariaSession3End = mariaSession3Start.plusMinutes(58);
      insertWorkoutSession(conn, WORKOUT_SESSION_MARIA_3, MARIA_MEMBER_ID, ROUTINE_MARIA_FULL,
          mariaSession3Start, mariaSession3End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_3, EXERCISE_DEADLIFT, 3, 8, 60.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_3, EXERCISE_SQUAT, 3, 10, 50.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_3, EXERCISE_PULL_UP, 3, 6, null);
      System.out.println("    ✓ Maria - Full Body (10 days ago)");
      
      // Maria Session 4: Cardio - 14 days ago
      LocalDateTime mariaSession4Start = now.minusDays(14).withHour(6).withMinute(0);
      LocalDateTime mariaSession4End = mariaSession4Start.plusMinutes(38);
      insertWorkoutSession(conn, WORKOUT_SESSION_MARIA_4, MARIA_MEMBER_ID, ROUTINE_MARIA_CARDIO,
          mariaSession4Start, mariaSession4End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_4, EXERCISE_RUNNING, 1, 12, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_4, EXERCISE_PLANK, 3, 35, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_4, EXERCISE_CRUNCHES, 3, 16, null);
      System.out.println("    ✓ Maria - Cardio (14 days ago)");
      
      // Maria Session 5: Full Body - 19 days ago
      LocalDateTime mariaSession5Start = now.minusDays(19).withHour(8).withMinute(0);
      LocalDateTime mariaSession5End = mariaSession5Start.plusMinutes(52);
      insertWorkoutSession(conn, WORKOUT_SESSION_MARIA_5, MARIA_MEMBER_ID, ROUTINE_MARIA_FULL,
          mariaSession5Start, mariaSession5End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_5, EXERCISE_BENCH_PRESS, 3, 10, 35.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_5, EXERCISE_SQUAT, 3, 10, 47.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_5, EXERCISE_PLANK, 3, 32, null);
      System.out.println("    ✓ Maria - Full Body (19 days ago)");
      
      // Maria Session 6: Cardio - 25 days ago
      LocalDateTime mariaSession6Start = now.minusDays(25).withHour(7).withMinute(0);
      LocalDateTime mariaSession6End = mariaSession6Start.plusMinutes(35);
      insertWorkoutSession(conn, WORKOUT_SESSION_MARIA_6, MARIA_MEMBER_ID, ROUTINE_MARIA_CARDIO,
          mariaSession6Start, mariaSession6End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_6, EXERCISE_RUNNING, 1, 10, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_MARIA_6, EXERCISE_BURPEES, 3, 10, null);
      System.out.println("    ✓ Maria - Cardio (25 days ago)");
      
      // ==========================================
      // PEDRO's Workout Sessions (10 sessions - excellent adherence)
      // Target: 5/week, 30 days = ~21.4 expected, 10 done = 46.7% adherence
      // ==========================================
      System.out.println("  Creating Pedro's workout sessions...");
      
      // Pedro Session 1: Push Day - 1 day ago
      LocalDateTime pedroSession1Start = now.minusDays(1).withHour(5).withMinute(30);
      LocalDateTime pedroSession1End = pedroSession1Start.plusMinutes(75);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_1, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PUSH,
          pedroSession1Start, pedroSession1End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_1, EXERCISE_BENCH_PRESS, 5, 7, 85.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_1, EXERCISE_SHOULDER_PRESS, 4, 9, 52.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_1, EXERCISE_CABLE_FLY, 3, 14, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_1, EXERCISE_TRICEP_DIPS, 4, 9, null);
      System.out.println("    ✓ Pedro - Push Day (1 day ago)");
      
      // Pedro Session 2: Pull Day - 3 days ago
      LocalDateTime pedroSession2Start = now.minusDays(3).withHour(6).withMinute(0);
      LocalDateTime pedroSession2End = pedroSession2Start.plusMinutes(80);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_2, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PULL,
          pedroSession2Start, pedroSession2End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_2, EXERCISE_DEADLIFT, 5, 5, 125.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_2, EXERCISE_PULL_UP, 4, 10, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_2, EXERCISE_BARBELL_ROW, 4, 9, 72.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_2, EXERCISE_BARBELL_CURL, 4, 11, 32.5);
      System.out.println("    ✓ Pedro - Pull Day (3 days ago)");
      
      // Pedro Session 3: Push Day - 5 days ago
      LocalDateTime pedroSession3Start = now.minusDays(5).withHour(5).withMinute(45);
      LocalDateTime pedroSession3End = pedroSession3Start.plusMinutes(72);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_3, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PUSH,
          pedroSession3Start, pedroSession3End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_3, EXERCISE_BENCH_PRESS, 5, 6, 82.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_3, EXERCISE_SHOULDER_PRESS, 4, 8, 50.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_3, EXERCISE_LATERAL_RAISE, 4, 13, 12.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_3, EXERCISE_TRICEP_PUSHDOWN, 3, 14, null);
      System.out.println("    ✓ Pedro - Push Day (5 days ago)");
      
      // Pedro Session 4: Pull Day - 8 days ago
      LocalDateTime pedroSession4Start = now.minusDays(8).withHour(6).withMinute(0);
      LocalDateTime pedroSession4End = pedroSession4Start.plusMinutes(78);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_4, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PULL,
          pedroSession4Start, pedroSession4End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_4, EXERCISE_DEADLIFT, 5, 6, 120.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_4, EXERCISE_PULL_UP, 4, 9, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_4, EXERCISE_FACE_PULL, 3, 17, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_4, EXERCISE_HAMMER_CURL, 3, 13, 15.0);
      System.out.println("    ✓ Pedro - Pull Day (8 days ago)");
      
      // Pedro Session 5: Push Day - 10 days ago
      LocalDateTime pedroSession5Start = now.minusDays(10).withHour(5).withMinute(30);
      LocalDateTime pedroSession5End = pedroSession5Start.plusMinutes(70);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_5, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PUSH,
          pedroSession5Start, pedroSession5End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_5, EXERCISE_BENCH_PRESS, 5, 7, 80.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_5, EXERCISE_SHOULDER_PRESS, 4, 9, 47.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_5, EXERCISE_TRICEP_DIPS, 4, 8, null);
      System.out.println("    ✓ Pedro - Push Day (10 days ago)");
      
      // Pedro Session 6: Pull Day - 12 days ago
      LocalDateTime pedroSession6Start = now.minusDays(12).withHour(6).withMinute(15);
      LocalDateTime pedroSession6End = pedroSession6Start.plusMinutes(82);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_6, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PULL,
          pedroSession6Start, pedroSession6End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_6, EXERCISE_DEADLIFT, 5, 5, 117.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_6, EXERCISE_BARBELL_ROW, 4, 9, 70.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_6, EXERCISE_BARBELL_CURL, 4, 10, 30.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_6, EXERCISE_HAMMER_CURL, 3, 12, 14.0);
      System.out.println("    ✓ Pedro - Pull Day (12 days ago)");
      
      // Pedro Session 7: Push Day - 15 days ago
      LocalDateTime pedroSession7Start = now.minusDays(15).withHour(5).withMinute(45);
      LocalDateTime pedroSession7End = pedroSession7Start.plusMinutes(68);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_7, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PUSH,
          pedroSession7Start, pedroSession7End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_7, EXERCISE_BENCH_PRESS, 5, 6, 77.5);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_7, EXERCISE_CABLE_FLY, 3, 12, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_7, EXERCISE_LATERAL_RAISE, 4, 12, 11.0);
      System.out.println("    ✓ Pedro - Push Day (15 days ago)");
      
      // Pedro Session 8: Pull Day - 18 days ago
      LocalDateTime pedroSession8Start = now.minusDays(18).withHour(6).withMinute(0);
      LocalDateTime pedroSession8End = pedroSession8Start.plusMinutes(76);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_8, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PULL,
          pedroSession8Start, pedroSession8End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_8, EXERCISE_DEADLIFT, 5, 5, 115.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_8, EXERCISE_PULL_UP, 4, 8, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_8, EXERCISE_BARBELL_ROW, 4, 8, 67.5);
      System.out.println("    ✓ Pedro - Pull Day (18 days ago)");
      
      // Pedro Session 9: Push Day - 22 days ago
      LocalDateTime pedroSession9Start = now.minusDays(22).withHour(5).withMinute(30);
      LocalDateTime pedroSession9End = pedroSession9Start.plusMinutes(72);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_9, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PUSH,
          pedroSession9Start, pedroSession9End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_9, EXERCISE_BENCH_PRESS, 5, 6, 75.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_9, EXERCISE_SHOULDER_PRESS, 4, 8, 45.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_9, EXERCISE_TRICEP_DIPS, 4, 7, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_9, EXERCISE_TRICEP_PUSHDOWN, 3, 12, null);
      System.out.println("    ✓ Pedro - Push Day (22 days ago)");
      
      // Pedro Session 10: Pull Day - 26 days ago
      LocalDateTime pedroSession10Start = now.minusDays(26).withHour(6).withMinute(0);
      LocalDateTime pedroSession10End = pedroSession10Start.plusMinutes(80);
      insertWorkoutSession(conn, WORKOUT_SESSION_PEDRO_10, PEDRO_MEMBER_ID, ROUTINE_PEDRO_PULL,
          pedroSession10Start, pedroSession10End);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_10, EXERCISE_DEADLIFT, 5, 5, 110.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_10, EXERCISE_PULL_UP, 4, 7, null);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_10, EXERCISE_BARBELL_ROW, 4, 8, 65.0);
      insertWorkoutItem(conn, UUID.randomUUID(), WORKOUT_SESSION_PEDRO_10, EXERCISE_BARBELL_CURL, 4, 10, 27.5);
      System.out.println("    ✓ Pedro - Pull Day (26 days ago)");
      
      // ==========================================
      // Update adherence rates for members
      // ==========================================
      // Note: Adherence is pro-rated based on user's created_at date.
      // Since these members were created 60 days ago (> 30 day window),
      // the full 30-day window is used for calculation.
      // Formula: (totalWorkouts / (targetPerWeek * 4.29)) * 100
      System.out.println("  Calculating adherence rates...");
      
      // Alice: 7 workouts in 30 days, target is 3/week (~12.9 expected)
      // Adherence = (7 / 12.9) * 100 ≈ 54.3%
      updateUserAdherenceRate(conn, ALICE_MEMBER_ID, 54.26);
      System.out.println("    ✓ Alice adherence rate: 54.26%");
      
      // Bob: 5 workouts in 30 days, target is 4/week (~17.1 expected)
      // Adherence = (5 / 17.1) * 100 ≈ 29.2%
      updateUserAdherenceRate(conn, BOB_MEMBER_ID, 29.24);
      System.out.println("    ✓ Bob adherence rate: 29.24%");
      
      // João: 8 workouts in 30 days, target is 4/week (~17.1 expected)
      // Adherence = (8 / 17.1) * 100 ≈ 46.8%
      updateUserAdherenceRate(conn, JOAO_MEMBER_ID, 46.78);
      System.out.println("    ✓ João adherence rate: 46.78%");
      
      // Maria: 6 workouts in 30 days, target is 3/week (~12.9 expected)
      // Adherence = (6 / 12.9) * 100 ≈ 46.5%
      updateUserAdherenceRate(conn, MARIA_MEMBER_ID, 46.51);
      System.out.println("    ✓ Maria adherence rate: 46.51%");
      
      // Pedro: 10 workouts in 30 days, target is 5/week (~21.4 expected)
      // Adherence = (10 / 21.4) * 100 ≈ 46.7%
      updateUserAdherenceRate(conn, PEDRO_MEMBER_ID, 46.73);
      System.out.println("    ✓ Pedro adherence rate: 46.73%");
      
      // Commit transaction
      conn.commit();
      System.out.println("  ✓ Successfully seeded 36 workout sessions with 131 total exercises logged");
      
    } catch (SQLException e) {
      // Rollback on error
      conn.rollback();
      throw e;
    } finally {
      conn.setAutoCommit(true);
    }
  }
  
  private static void insertWorkoutSession(
      Connection conn,
      UUID id,
      UUID memberId,
      UUID routineId,
      LocalDateTime startedAt,
      LocalDateTime endedAt
  ) throws SQLException {
    String sql = "INSERT INTO workout_sessions (id, member_id, routine_id, started_at, ended_at, created_at, updated_at) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    LocalDateTime now = LocalDateTime.now();
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setObject(2, memberId);
      stmt.setObject(3, routineId);
      stmt.setTimestamp(4, Timestamp.valueOf(startedAt));
      stmt.setTimestamp(5, endedAt != null ? Timestamp.valueOf(endedAt) : null);
      stmt.setTimestamp(6, Timestamp.valueOf(now));
      stmt.setTimestamp(7, Timestamp.valueOf(now));
      
      stmt.executeUpdate();
    }
  }
  
  private static void insertWorkoutItem(
      Connection conn,
      UUID id,
      UUID workoutSessionId,
      UUID exerciseId,
      Integer setsDone,
      Integer repsDone,
      Double loadUsed
  ) throws SQLException {
    String sql = "INSERT INTO workout_items (id, workout_session_id, exercise_id, sets_done, reps_done, load_used) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);
      stmt.setObject(2, workoutSessionId);
      stmt.setObject(3, exerciseId);
      stmt.setInt(4, setsDone);
      stmt.setInt(5, repsDone);
      if (loadUsed != null) {
        stmt.setDouble(6, loadUsed);
      } else {
        stmt.setNull(6, java.sql.Types.DOUBLE);
      }
      
      stmt.executeUpdate();
    }
  }
  
  private static void updateUserAdherenceRate(
      Connection conn,
      UUID userId,
      Double adherenceRate
  ) throws SQLException {
    String sql = "UPDATE users SET adherence_rate = ? WHERE id = ?";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setDouble(1, adherenceRate);
      stmt.setObject(2, userId);
      
      stmt.executeUpdate();
    }
  }
}

