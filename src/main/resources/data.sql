-- TRUNCATE TABLE users, seller_profiles, comments, ratings, games, game_objects;

INSERT INTO users (id, first_name, last_name, password, email, role, verified)
VALUES (0, 'Anonymous', 'Anonymous', '', '', 'ANONYM', TRUE);
INSERT INTO users (first_name, last_name, password, email, role, verified)
VALUES ('Admin', 'Admin', 'admin', 'admin@gmail.com', 'ADMIN', TRUE);
INSERT INTO users (first_name, last_name, password, email, role, verified)
VALUES ('Seller', 'Seller', 'seller', 'seller@gmail.com', 'SELLER', TRUE);
INSERT INTO users (first_name, last_name, password, email, role, verified)
VALUES ('TestSeller', 'TestSeller', 'testseller', 'testseller@gmail.com', 'SELLER', TRUE);

INSERT INTO seller_profiles (user_id, nickname, description, status)
VALUES (2, 'Test seller 1', 'Test seller 1 description', 'APPROVED');
INSERT INTO ratings (seller_id, avg_rating, rating_sum, comments_count)
VALUES (1, 4.5, 9, 2);

INSERT INTO games (name, description)
VALUES ('Test game', 'Test game description');
INSERT INTO game_objects (title, description, seller_id, game_id)
VALUES ('Test game object', 'Test game object description', 1, 1);

INSERT INTO comments (author_id, seller_id, message, rating_mark, status)
VALUES (0, 1, 'Test comment 1', 5, 'APPROVED');
INSERT INTO comments (author_id, seller_id, message, rating_mark, status)
VALUES (1, 1, 'Test comment 2', 4, 'APPROVED');
INSERT INTO comments (author_id, seller_id, message, rating_mark, status)
VALUES (2, 1, 'Test comment 3', 3, 'REJECTED');
