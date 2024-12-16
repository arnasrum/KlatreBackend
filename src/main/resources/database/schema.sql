CREATE TABLE IF NOT EXISTS users(
    id serial,
    email text,
    name text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS testdata(
    id text,
    users serial,
    data text,
    PRIMARY KEY (id),
    FOREIGN KEY (users) REFERENCES users(id)
);