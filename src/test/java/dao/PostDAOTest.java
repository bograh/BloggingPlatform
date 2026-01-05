package dao;

import config.H2ConnectionProvider;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostDAOTest {

    private PostDAO postDAO;
    private H2ConnectionProvider provider;

    @BeforeEach
    void setup() throws Exception {
        provider = new H2ConnectionProvider();
        postDAO = new PostDAO(provider);

        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS post_tags");
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
                        CREATE TABLE posts (
                            id SERIAL PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            body TEXT NOT NULL,
                            author_id INT NOT NULL,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (author_id) REFERENCES users(id)
                        )
                    """);
            stmt.execute("""
                        CREATE TABLE post_tags (
                            post_id INT NOT NULL,
                            tag_id INT NOT NULL
                        )
                    """);
        }
    }

    @Test
    void addPost_shouldAddPost() throws Exception {
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, email, password) VALUES ('john', 'john@test.com', 'pass')");
        }

        Post post = new Post(0, "Title", "Body", 1, LocalDateTime.now(), LocalDateTime.now());
        postDAO.addPost(post, null);

        assertTrue(post.getId() > 0);

        List<PostResponseDTO> posts = postDAO.getAllPosts();
        assertEquals(1, posts.size());
    }

    @Test
    void getPostById_shouldReturnPost() throws Exception {
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, email, password) VALUES ('jane', 'jane@test.com', 'pass')");
            stmt.execute("INSERT INTO posts (title, body, author_id) VALUES ('Title1', 'Body1', 1)");
        }

        PostResponseDTO post = postDAO.getPostById(1);
        assertNotNull(post);
        assertEquals("Title1", post.getTitle());
    }

    @Test
    void updatePost_shouldUpdate() throws Exception {
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, email, password) VALUES ('jane', 'jane@test.com', 'pass')");
            stmt.execute("INSERT INTO posts (title, body, author_id) VALUES ('Title1', 'Body1', 1)");
        }

        UpdatePostDTO update = new UpdatePostDTO(1, "New Title", "New Body", LocalDateTime.now());
        postDAO.updatePost(update, 1);

        PostResponseDTO post = postDAO.getPostById(1);
        assertEquals("New Title", post.getTitle());
    }

    @Test
    void updatePost_notAuthor_shouldThrow() {
        Exception exception = assertThrows(ForbiddenException.class, () -> {
            UpdatePostDTO update = new UpdatePostDTO(1, "New Title", "New Body", LocalDateTime.now());
            postDAO.updatePost(update, 999);
        });
        assertTrue(exception.getMessage().contains("Forbidden"));
    }

    @Test
    void deletePost_shouldDelete() throws Exception {
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, email, password) VALUES ('jane', 'jane@test.com', 'pass')");
            stmt.execute("INSERT INTO posts (title, body, author_id) VALUES ('Title1', 'Body1', 1)");
        }

        postDAO.deletePost(1, 1);
        assertThrows(PostNotFoundException.class, () -> postDAO.getPostById(1));
    }
}
