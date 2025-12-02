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
  private static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID JOHN_COACH_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID SARAH_COACH_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final UUID ALICE_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
  private static final UUID BOB_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
  private static final UUID CHARLIE_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
  private static final UUID DAVID_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
  
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
  
  private static void clearExistingData(Connection conn) throws SQLException {
    // Delete in correct order to respect foreign key constraints
    String sql = "DELETE FROM users";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
}

