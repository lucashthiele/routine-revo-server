-- Add adherence_rate column to users table
ALTER TABLE users 
ADD COLUMN adherence_rate DECIMAL(5, 2);

-- Create workout_sessions table
CREATE TABLE workout_sessions (
    id              UUID PRIMARY KEY,
    member_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    routine_id      UUID REFERENCES routines(id) ON DELETE SET NULL,
    started_at      TIMESTAMP NOT NULL,
    ended_at        TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Create workout_items table
CREATE TABLE workout_items (
    id                  UUID PRIMARY KEY,
    workout_session_id  UUID NOT NULL REFERENCES workout_sessions(id) ON DELETE CASCADE,
    exercise_id         UUID NOT NULL REFERENCES exercises(id),
    sets_done           INTEGER NOT NULL,
    reps_done           INTEGER NOT NULL,
    load_used           DECIMAL(10, 2)
);

-- Indexes for performance
CREATE INDEX idx_workout_sessions_member_id ON workout_sessions(member_id);
CREATE INDEX idx_workout_sessions_routine_id ON workout_sessions(routine_id);
CREATE INDEX idx_workout_sessions_started_at ON workout_sessions(started_at);
CREATE INDEX idx_workout_items_session_id ON workout_items(workout_session_id);
CREATE INDEX idx_workout_items_exercise_id ON workout_items(exercise_id);

-- Timestamp trigger for workout_sessions
CREATE TRIGGER set_timestamp_workout_sessions
BEFORE UPDATE ON workout_sessions
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

