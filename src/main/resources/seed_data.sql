INSERT INTO users (username, email, password)
VALUES ('ben', 'ben@email.com', 'password1'),
       ('tim', 'tim@email.com', 'password2');

INSERT INTO tags (name)
VALUES ('Java'),
       ('PostgreSQL'),
       ('JavaFX');

INSERT INTO posts (title, body, author_id)
VALUES ('Intro to JavaFX', 'JavaFX tutorial content...', 1),
       ('Database Indexing', 'Indexing in PostgreSQL explained...', 1),
       ('REST API Design', 'Best practices for designing RESTful APIs...', 1),
       ('Spring Boot Security', 'Introduction to authentication and authorization in Spring Boot...', 1),
       ('JUnit Testing', 'Writing unit and integration tests with JUnit...', 1);


INSERT INTO post_tags (post_id, tag_id)
VALUES (1, 3),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 2);

INSERT INTO comments (post_id, author_id, content)
VALUES (1, 1, 'Great tutorial!'),
       (4, 1, 'Very helpful, thanks.'),
       (1, 1, 'Looking forward to the next part of this series.'),
       (4, 1, 'The examples made indexing much clearer.'),
       (3, 1, 'This clarified a lot of confusion I had about APIs.');


INSERT INTO reviews (post_id, reviewer_id, ratings, review)
VALUES (1, 1, 5, 'Excellent article!'),
       (5, 1, 4, 'Useful explanation of indexing.');
