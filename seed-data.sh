#!/bin/bash

# Standalone Data Seeder Script
# This script runs the data seeder independently from Spring Boot
#
# Usage:
#   ./seed-data.sh          # Interactive mode - will prompt before clearing data
#   ./seed-data.sh --force  # Force mode - will clear and reseed without prompting

set -e  # Exit on error

echo "========================================="
echo "  Routine Revo - Data Seeder"
echo "========================================="
echo ""

# Check if Docker container is running
if ! docker ps | grep -q routine_revo_pg; then
  echo "⚠ Warning: PostgreSQL container 'routine_revo_pg' is not running."
  echo "Starting PostgreSQL container..."
  docker-compose up -d postgres
  echo "Waiting for PostgreSQL to be ready..."
  sleep 5
fi

echo "Compiling project..."
./mvnw compile -q

echo ""
echo "Running database migrations..."
./mvnw flyway:migrate -q
echo "✓ Migrations completed"

echo ""
echo "Running data seeder..."
echo ""

# Pass arguments to the Java program
ARGS=""
if [ "$1" = "--force" ]; then
  ARGS="--force"
fi

# Run the seeder using Maven exec plugin
./mvnw exec:java \
  -Dexec.mainClass="com.lucashthiele.routine_revo_server.tools.StandaloneDataSeeder" \
  -Dexec.classpathScope=runtime \
  -Dexec.args="$ARGS" \
  -q

echo ""
echo "✓ Seeding process completed"

