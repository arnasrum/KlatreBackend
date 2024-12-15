CREATE TABLE users(
    id text,
    username text,
    password text,
    PRIMARY KEY (id)
);

CREATE TABLE testdata(
    id text,
    users text,
    data text,
    PRIMARY KEY (id),
    FOREIGN KEY (users) REFERENCES users(id)
);