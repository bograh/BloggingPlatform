INSERT INTO users (username, email, password)
VALUES ('ben', 'ben@email.com', 'password1'),
       ('tim', 'tim@email.com', 'password2');

INSERT INTO tags (name)
VALUES ('Java'),
       ('PostgreSQL'),
       ('JavaFX');

INSERT INTO posts (title, body, author_id)
VALUES ('Intro to JavaFX', 'JavaFX tutorial content...', 1),
       ('Database Indexing', 'Indexing in PostgreSQL explained...', 2);

INSERT INTO post_tags (post_id, tag_id)
VALUES (1, 3),
       (2, 2);

INSERT INTO comments (post_id, author_id, content)
VALUES (1, 2, 'Great tutorial!'),
       (2, 1, 'Very helpful, thanks.');

INSERT INTO reviews (post_id, reviewer_id, ratings, review)
VALUES (1, 2, 5, 'Excellent article!'),
       (2, 1, 4, 'Useful explanation of indexing.');
