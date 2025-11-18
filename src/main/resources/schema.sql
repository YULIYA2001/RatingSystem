-- DROP TABLE users, seller_profiles, comments, ratings, games, game_objects;
-- DROP TYPE role, status;

CREATE TYPE role AS ENUM ('ANONYM', 'SELLER', 'ADMIN');

CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role       role         NOT NULL DEFAULT 'SELLER',
    verified   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TYPE status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

CREATE TABLE seller_profiles
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     INTEGER     NOT NULL REFERENCES users (id),
    nickname    VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      status      NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE comments
(
    id              BIGSERIAL PRIMARY KEY,
    author_id       INTEGER   NOT NULL REFERENCES users (id),
    seller_id       INTEGER   NOT NULL REFERENCES seller_profiles (id),
    message         TEXT,
    rating_mark     INTEGER   NOT NULL CHECK (rating_mark BETWEEN 1 AND 5),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          status    NOT NULL DEFAULT 'PENDING',
    verified_seller BOOLEAN   NOT NULL DEFAULT TRUE
);

CREATE TABLE ratings
(
    id             BIGSERIAL PRIMARY KEY,
    seller_id      INTEGER       NOT NULL REFERENCES seller_profiles (id),
    avg_rating     DECIMAL(3, 2) NOT NULL DEFAULT 0,
    rating_sum     INTEGER       NOT NULL DEFAULT 0,
    comments_count INTEGER       NOT NULL DEFAULT 0
);

CREATE TABLE games
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE game_objects
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    seller_id   INTEGER     NOT NULL REFERENCES seller_profiles (id),
    game_id     INTEGER     NOT NULL REFERENCES games (id)
);
