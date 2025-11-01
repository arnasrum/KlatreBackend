DROP TABLE IF EXISTS climbing_sessions, group_invites, user_groups, image, routes, klatre_groups, users, roles, places, route_sends, route_attempts, grading_systems, grades CASCADE;

CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS roles(
    id INT PRIMARY KEY,
    role_name TEXT
);

CREATE TABLE IF NOT EXISTS klatre_groups(
    id BIGSERIAL PRIMARY KEY,
    owner BIGINT REFERENCES users(id) ON DELETE SET NULL,
    name TEXT NOT NULL,
    personal BOOL,
    uuid TEXT NOT NULL DEFAULT gen_random_uuid(),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS grading_systems(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    climb_type VARCHAR(50) NOT NULL,
    is_global BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_in_group BIGINT REFERENCES klatre_groups(id) ON DELETE CASCADE
    --created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
);

CREATE TABLE IF NOT EXISTS grades(
    id BIGSERIAL PRIMARY KEY,
    system_id BIGSERIAL REFERENCES grading_systems(id) ON DELETE CASCADE,
    grade_string VARCHAR(50) NOT NULL,
    numerical_value INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(system_id, grade_string)
);

CREATE TABLE IF NOT EXISTS places(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    grading_system_id BIGINT REFERENCES grading_systems(id) ON DELETE CASCADE DEFAULT 1,
    group_id BIGSERIAL REFERENCES klatre_groups(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS image(
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
    content_type TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    file_size BIGINT NOT NULL,
    user_id BIGSERIAL REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS routes(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    grade_id BIGINT REFERENCES grades(id),
    description TEXT,
    active BOOL NOT NULL DEFAULT true,
    user_id BIGINT REFERENCES users(id),
    date_added TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    place_id BIGSERIAL REFERENCES places(id) ON DELETE CASCADE,
    image_id TEXT REFERENCES image(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_groups(
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id BIGINT NOT NULL REFERENCES klatre_groups(id) ON DELETE CASCADE,
    role INT NOT NULL references roles(id) ON DELETE CASCADE, -- OWNER, ADMIN, USER
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY(user_id, group_id)
);

CREATE TABLE IF NOT EXISTS group_invites(
    id BIGSERIAL PRIMARY KEY,
    group_id BIGSERIAL REFERENCES klatre_groups(id) ON DELETE CASCADE,
    user_id BIGSERIAL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'pending', -- 'pending', 'accepted', 'declined', 'revoked', 'expired'
    invited_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    accepted_at TIMESTAMP WITH TIME ZONE,
    declined_at TIMESTAMP WITH TIME ZONE,
    revoked_at TIMESTAMP WITH TIME ZONE
    --UNIQUE (group_id, user_id) WHERE status = 'pending'
);

CREATE TABLE IF NOT EXISTS boulder_equivalence(
    id BIGSERIAL PRIMARY KEY,
    boulder_id1 BIGSERIAL REFERENCES routes(id) NOT NULL, -- Take sends from boulder2 and apply to boulder1
    boulder_id2 BIGSERIAL REFERENCES routes(id) NOT NULL,
    user_id BIGSERIAL REFERENCES users(id) NOT NULL,
    UNIQUE(boulder_id1, boulder_id2)
);


CREATE TABLE IF NOT EXISTS climbing_sessions(
    id BIGSERIAL PRIMARY KEY,
    active BOOL DEFAULT true,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    group_id BIGINT REFERENCES klatre_groups(id) ON DELETE SET NULL,
    place_id BIGINT REFERENCES places(id) ON DELETE SET NULL,
    created_at BIGINT DEFAULT FLOOR(EXTRACT(EPOCH FROM NOW()))::BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS route_attempts(
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT REFERENCES routes(id) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    completed BOOL DEFAULT false,
    session BIGINT REFERENCES climbing_sessions(id) ON DELETE CASCADE,
    last_updated BIGINT NOT NULL
);

DROP INDEX IF EXISTS active_sessions_user_group_active_idx;
CREATE UNIQUE INDEX active_sessions_user_group_active_idx ON climbing_sessions (user_id, group_id)
    WHERE active;

CREATE UNIQUE INDEX idx_unique_pending_invite ON group_invites (group_id, user_id)
    WHERE status = 'pending';