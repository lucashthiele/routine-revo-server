ALTER TYPE user_role RENAME TO user_role_old;

CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'COACH',
    'MEMBER'
);

ALTER TABLE users
ALTER COLUMN role TYPE user_role 
USING (
    CASE 
        WHEN role::text = 'INSTRUCTOR' THEN 'COACH'::user_role
        ELSE role::text::user_role 
    END
);

DROP TYPE user_role_old;

ALTER TABLE users 
ADD COLUMN coach_id UUID;

ALTER TABLE users 
ADD COLUMN workout_per_week INTEGER;

ALTER TABLE users 
ADD CONSTRAINT fk_users_coach 
FOREIGN KEY (coach_id) 
REFERENCES users (id)
ON DELETE SET NULL;

CREATE OR REPLACE FUNCTION check_coach_role()
RETURNS TRIGGER AS $$
DECLARE
    target_role user_role;
BEGIN
    IF NEW.coach_id IS NOT NULL THEN
        SELECT role INTO target_role 
        FROM users 
        WHERE id = NEW.coach_id;

        IF target_role <> 'COACH' THEN
            RAISE EXCEPTION 'The user referenced as coach_id must have the role COACH.';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_coach_selection
BEFORE INSERT OR UPDATE ON users
FOR EACH ROW
EXECUTE PROCEDURE check_coach_role();
