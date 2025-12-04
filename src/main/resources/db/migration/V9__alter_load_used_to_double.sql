-- Change load_used from DECIMAL to DOUBLE PRECISION for Hibernate compatibility
ALTER TABLE workout_items 
ALTER COLUMN load_used TYPE DOUBLE PRECISION;

