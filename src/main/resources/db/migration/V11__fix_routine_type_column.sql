-- Fix routine_type column: convert from PostgreSQL enum to VARCHAR for Hibernate compatibility
-- This migration handles the case where V10 already created the enum type

-- Step 1: Drop the default value that depends on the enum type
ALTER TABLE routines ALTER COLUMN routine_type DROP DEFAULT;

-- Step 2: Alter the column to use VARCHAR (requires casting through text)
ALTER TABLE routines 
  ALTER COLUMN routine_type TYPE VARCHAR(20) USING routine_type::text;

-- Step 3: Set the new default value as a string
ALTER TABLE routines ALTER COLUMN routine_type SET DEFAULT 'CUSTOM';

-- Step 4: Drop the enum type
DROP TYPE IF EXISTS routine_type;

-- Step 5: Add check constraint to ensure only valid values
ALTER TABLE routines ADD CONSTRAINT chk_routine_type CHECK (routine_type IN ('TEMPLATE', 'CUSTOM'));

