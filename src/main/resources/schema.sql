DROP TABLE IF EXISTS users, seller_profiles, comments, ratings, games, game_objects;

-- DROP TYPE role, status;
-- CREATE TYPE role AS ENUM ('ANONYM', 'SELLER', 'ADMIN');

CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role       VARCHAR(20)  NOT NULL DEFAULT 'SELLER' CHECK (role IN ('ANONYM', 'SELLER', 'ADMIN')),
    verified   BOOLEAN      NOT NULL DEFAULT FALSE
);

-- CREATE TYPE status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

CREATE TABLE seller_profiles
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT UNIQUE REFERENCES users (id),
    nickname    VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE comments
(
    id              BIGSERIAL PRIMARY KEY,
    author_id       BIGINT      NOT NULL REFERENCES users (id),
    seller_id       BIGINT      NOT NULL REFERENCES seller_profiles (id),
    message         TEXT,
    rating_mark     INTEGER     NOT NULL CHECK (rating_mark BETWEEN 1 AND 5),
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    verified_seller BOOLEAN     NOT NULL DEFAULT TRUE
);

ALTER TABLE comments
ADD CONSTRAINT unique_seller_author UNIQUE (seller_id, author_id);

CREATE TABLE ratings
(
    id             BIGSERIAL PRIMARY KEY,
    seller_id      BIGINT        NOT NULL REFERENCES seller_profiles (id),
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
    title       VARCHAR(50) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    seller_id   BIGINT      NOT NULL REFERENCES seller_profiles (id),
    game_id     BIGINT      NOT NULL REFERENCES games (id)
);
