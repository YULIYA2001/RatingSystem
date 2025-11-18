TRUNCATE TABLE users, seller_profiles, comments, ratings, games, game_objects;

INSERT INTO users (id, first_name, last_name, password, email, role, verified)
VALUES (0, 'Anonymous', 'Anonymous', '', '', 'ANONYM', TRUE);
INSERT INTO users (first_name, last_name, password, email, role, verified)
VALUES ('Admin', 'Admin', 'admin', 'admin@gmail.com', 'ADMIN', TRUE);
INSERT INTO users (first_name, last_name, password, email, role, verified)
VALUES ('Seller', 'Seller', 'seller', 'seller@gmail.com', 'SELLER', TRUE);


