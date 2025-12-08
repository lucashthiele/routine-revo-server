-- Add routine_type enum to distinguish between template and custom routines
CREATE TYPE routine_type AS ENUM ('TEMPLATE', 'CUSTOM');

-- Add routine_type column with default CUSTOM for existing data
-- Existing routines with member_id = NULL will be updated to TEMPLATE
ALTER TABLE routines ADD COLUMN routine_type routine_type NOT NULL DEFAULT 'CUSTOM';

-- Add template_id to track which template a routine was created from
ALTER TABLE routines ADD COLUMN template_id UUID REFERENCES routines(id);

-- Update existing routines: those without a member are templates
UPDATE routines SET routine_type = 'TEMPLATE' WHERE member_id IS NULL;

-- Create indexes for efficient querying
CREATE INDEX idx_routines_routine_type ON routines(routine_type);
CREATE INDEX idx_routines_template_id ON routines(template_id);

