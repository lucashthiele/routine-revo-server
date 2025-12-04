-- Change DECIMAL columns to DOUBLE PRECISION for Hibernate compatibility
ALTER TABLE users 
ALTER COLUMN adherence_rate TYPE DOUBLE PRECISION;

ALTER TABLE workout_items 
ALTER COLUMN load_used TYPE DOUBLE PRECISION;

