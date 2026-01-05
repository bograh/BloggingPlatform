package services;

import config.H2ConnectionProvider;
import dao.PostDAO;
import dao.TagDAO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PostServiceTest {

    private PostService postService;
    private PostDAO postDAO;
    private TagDAO tagDAO;

    @BeforeEach
    void setup() throws Exception {
        H2ConnectionProvider provider = new H2ConnectionProvider();
        postDAO = new PostDAO(provider);
        tagDAO = new TagDAO(provider);

        User user = new User(1, "test", "test@test.com", "pass", null);
        postService = new PostService(user, postDAO, tagDAO);

        try (var conn = provider.getConnection();
             var stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS posts");
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("""
                        CREATE TABLE users (
                            id SERIAL PRIMARY KEY,
                            username VARCHAR(255) NOT NULL,
                            email VARCHAR(255) NOT NULL,
                            password VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                        )
                    """);
            stmt.execute("""
                        INSERT INTO users (username, email, password) VALUES ('test', 'test@test.com', 'pass')
                    """);
            stmt.execute("""
                        CREATE TABLE posts (
                            id SERIAL PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            body TEXT NOT NULL,
                            author_id INT NOT NULL,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (author_id) REFERENCES users(id)
                        )
                    """);
        }
    }

    @Test
    void createPost_shouldWork() {
        postService.createPost();
        assertDoesNotThrow(() -> {
            postDAO.getAllPosts();
        });
    }

    @Test
    void getAllPosts_shouldPrintPosts() {
        postService.createPost();
        postService.getAllPosts();
    }
}
