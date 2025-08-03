DROP TABLE IF EXISTS group_invites, user_groups, image, boulders, team_groups, users, roles CASCADE;

CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL,
    email TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS boulders(
    id BIGSERIAL,
    name TEXT,
    attempts INT,
    grade TEXT,
    description TEXT,
    userID BIGSERIAL,
    PRIMARY KEY (id),
    FOREIGN KEY (userID) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS image(
    id BIGSERIAL,
    image_base64 TEXT,
    boulderID BIGSERIAL UNIQUE,
    PRIMARY KEY(id),
    FOREIGN KEY (boulderID) REFERENCES boulders(id)
);

CREATE TABLE IF NOT EXISTS team_groups(
    id BIGSERIAL PRIMARY KEY,
    owner BIGSERIAL REFERENCES users(id) ON DELETE CASCADE,
    name TEXT,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS roles(
    id INT PRIMARY KEY,
    role_name TEXT
);

CREATE TABLE IF NOT EXISTS user_groups(
    user_id BIGSERIAL NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id BIGSERIAL NOT NULL REFERENCES team_groups(id) ON DELETE CASCADE,
    role INT references roles(id) ON DELETE CASCADE, -- OWNER, ADMIN, USER
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY(user_id, group_id)
);

CREATE TABLE IF NOT EXISTS group_invites(
    id BIGSERIAL PRIMARY KEY,
    group_id BIGSERIAL REFERENCES team_groups(id) ON DELETE CASCADE,
    user_id BIGSERIAL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'pending', -- 'pending', 'accepted', 'declined', 'revoked', 'expired'
    invited_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    accepted_at TIMESTAMP WITH TIME ZONE,
    declined_at TIMESTAMP WITH TIME ZONE,
    revoked_at TIMESTAMP WITH TIME ZONE
    --UNIQUE (group_id, user_id) WHERE status = 'pending'
);

CREATE UNIQUE INDEX idx_unique_pending_invite ON group_invites (group_id, user_id)
    WHERE status = 'pending';