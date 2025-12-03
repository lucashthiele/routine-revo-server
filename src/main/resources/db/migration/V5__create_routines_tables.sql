CREATE TABLE routines (
    id                  UUID PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    expiration_date     TIMESTAMP,
    creator_id          UUID NOT NULL REFERENCES users(id),
    member_id           UUID REFERENCES users(id),
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE routine_items (
    id                  UUID PRIMARY KEY,
    routine_id          UUID NOT NULL REFERENCES routines(id) ON DELETE CASCADE,
    exercise_id         UUID NOT NULL REFERENCES exercises(id),
    sets                INTEGER NOT NULL,
    reps                VARCHAR(50) NOT NULL,
    load                DECIMAL(10, 2),
    rest_time           VARCHAR(50),
    sequence_order      INTEGER NOT NULL,
    CONSTRAINT routine_items_routine_exercise_order_unique UNIQUE (routine_id, sequence_order)
);

CREATE INDEX idx_routines_creator_id ON routines(creator_id);
CREATE INDEX idx_routines_member_id ON routines(member_id);
CREATE INDEX idx_routine_items_routine_id ON routine_items(routine_id);
CREATE INDEX idx_routine_items_exercise_id ON routine_items(exercise_id);

CREATE TRIGGER set_timestamp_routines
BEFORE UPDATE ON routines
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

