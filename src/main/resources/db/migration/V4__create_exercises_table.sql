CREATE TYPE muscle_group AS ENUM (
    'CHEST',
    'BACK',
    'SHOULDERS',
    'BICEPS',
    'TRICEPS',
    'LEGS',
    'GLUTES',
    'ABS',
    'CARDIO',
    'FULL_BODY'
);

CREATE TABLE exercises (
    id              UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    muscle_group    muscle_group NOT NULL,
    description     TEXT,
    equipment       VARCHAR(255),
    image_url       VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_exercises_name ON exercises(name);
CREATE INDEX idx_exercises_muscle_group ON exercises(muscle_group);

CREATE TRIGGER set_timestamp_exercises
BEFORE UPDATE ON exercises
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

