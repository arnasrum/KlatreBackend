DROP TABLE IF EXISTS group_invites, user_groups, image, boulders, klatre_groups, users, roles, places, route_sends CASCADE;

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
    owner BIGSERIAL REFERENCES users(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    personal BOOL NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS places(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    group_id BIGSERIAL REFERENCES klatre_groups(id)
);

CREATE TABLE IF NOT EXISTS boulders(
    id BIGSERIAL PRIMARY KEY,
    name TEXT,
    grade TEXT,
    description TEXT,
    userID BIGSERIAL REFERENCES users(id),
    place BIGSERIAL REFERENCES places(id)
);

CREATE TABLE IF NOT EXISTS image(
    id BIGSERIAL,
    image_base64 TEXT,
    boulderID BIGSERIAL UNIQUE,
    PRIMARY KEY(id),
    FOREIGN KEY (boulderID) REFERENCES boulders(id)
);

CREATE TABLE IF NOT EXISTS user_groups(
    user_id BIGSERIAL NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id BIGSERIAL NOT NULL REFERENCES klatre_groups(id) ON DELETE CASCADE,
    role INT references roles(id) ON DELETE CASCADE, -- OWNER, ADMIN, USER
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

CREATE TABLE IF NOT EXISTS route_sends(
    id BIGSERIAL PRIMARY KEY,
    boulder BIGSERIAL REFERENCES boulders(id),
    date TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    attempts INT NOT NULL,
    perceived_grade TEXT
);

CREATE UNIQUE INDEX idx_unique_pending_invite ON group_invites (group_id, user_id)
    WHERE status = 'pending';
