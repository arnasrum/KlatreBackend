DROP TABLE users, boulders;

CREATE TABLE IF NOT EXISTS users(
    id serial,
    email text UNIQUE NOT NULL,
    name text NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS boulders(
    id SERIAL,
    name TEXT,
    attempts INT,
    grade text,
    image text,
    userID SERIAL,
    PRIMARY KEY (id),
    FOREIGN KEY (userID) REFERENCES users(id)
);
