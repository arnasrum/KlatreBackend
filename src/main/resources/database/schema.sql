DROP TABLE IF EXISTS users, boulders, image;

CREATE TABLE IF NOT EXISTS users(
    id SERIAL,
    email TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS boulders(
    id SERIAL,
    name TEXT,
    attempts INT,
    grade TEXT,
    description TEXT,
    userID SERIAL,
    PRIMARY KEY (id),
    FOREIGN KEY (userID) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS image(
    id SERIAL,
    image_base64 TEXT,
    boulderID SERIAL UNIQUE,
    PRIMARY KEY(id),
    FOREIGN KEY (boulderID) REFERENCES boulders(id)
);