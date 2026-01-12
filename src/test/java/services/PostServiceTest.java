package services;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dao.PostDAO;
import dao.TagDAO;
import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostServiceTest {

    private static final User TEST_USER =
            new User(1, "test", "test@test.com", "pass", null);

    private H2ConnectionProvider provider;
    private PostDAO postDAO;
    private PostService postService;

    @BeforeEach
    void setUp() throws Exception {
        provider = new H2ConnectionProvider();
        postDAO = new PostDAO(provider);
        TagDAO tagDAO = new TagDAO(provider);
        postService = new PostService(TEST_USER, postDAO, tagDAO);

        TestDatabaseSetup.reset(provider);

        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            seedData(stmt);
        }
    }

    @ParameterizedTest(name = "Create post with title={0}")
    @ValueSource(strings = {
            "First Post",
            "Second Post",
            "Third Post"
    })
    void createPost_withValueSource_shouldPersist(String title) throws Exception {
        CreatePostDTO dto = new CreatePostDTO(title, "Default body content");

        postService.createPost(dto, List.of("Java", "JavaFX"));

        var posts = postService.getAllPosts();
        assertTrue(posts.stream()
                        .anyMatch(p -> p.getTitle().equals(title)),
                "Post with title '" + title + "' should exist");
    }

    @Test
    void getAllPosts_whenNoPosts_shouldReturnEmptyList() throws Exception {
        assertTrue(postService.getAllPosts().isEmpty());
    }

    @Test
    void getPostById_shouldReturnPost() throws Exception {
        CreatePostDTO dto = new CreatePostDTO("Sample Post", "Sample Content");
        postService.createPost(dto, List.of("Java"));

        PostResponseDTO post = postService.getPostById(1);
        assertNotNull(post);
        assertEquals("Sample Post", post.getTitle());
    }

    @Test
    void getPostById_whenNotFound_shouldReturnNull() throws Exception {
        PostResponseDTO post = postService.getPostById(999);
        assertNull(post);
    }

    @Test
    void updatePost_shouldUpdatePost_whenAuthorMatches() throws Exception {
        CreatePostDTO dto = new CreatePostDTO("Old Title", "Old Content");
        postService.createPost(dto, List.of("Java"));

        UpdatePostDTO updateDTO = new UpdatePostDTO(
                1, "New Title", "New Content"
        );
        String response = postService.updatePost(updateDTO);
        assertTrue(response.contains("updated successfully"));

        PostResponseDTO updated = postService.getPostById(1);
        assertEquals("New Title", updated.getTitle());
    }

    @Test
    void updatePost_shouldFail_whenUserIsNotAuthor() throws Exception {
        CreatePostDTO dto = new CreatePostDTO("Old Title", "Old Content");
        postService.createPost(dto, List.of("Java"));

        User otherUser = new User(2, "other", "o@test.com", "pass", null);
        PostService otherService = new PostService(otherUser, postDAO, new TagDAO(provider));

        UpdatePostDTO updateDTO = new UpdatePostDTO(
                1, "New Title", "New Content");
        String response = otherService.updatePost(updateDTO);
        assertTrue(response.toLowerCase().contains("forbidden"));
    }

    @Test
    void deletePost_shouldDeletePost_whenAuthorMatches() throws Exception {
        CreatePostDTO dto = new CreatePostDTO("To Delete", "Some content");
        postService.createPost(dto, List.of("Java"));

        String response = postService.deletePost(1);
        assertTrue(response.contains("deleted successfully"));

        PostResponseDTO post = postService.getPostById(1);
        assertNull(post);
    }

    @Test
    void deletePost_shouldFail_whenUserIsNotAuthor() throws Exception {
        CreatePostDTO dto = new CreatePostDTO("To Delete", "Some content");
        postService.createPost(dto, List.of("Java"));

        User otherUser = new User(2, "other", "o@test.com", "pass", null);
        PostService otherService = new PostService(otherUser, postDAO, new TagDAO(provider));

        String response = otherService.deletePost(1);
        assertTrue(response.toLowerCase().contains("forbidden"));
    }

    @Test
    void searchTest_shouldReturnTotalResults() {
        postService.createPost(new CreatePostDTO("To Search", "Some content"), List.of("Java"));
        postService.createPost(new CreatePostDTO("Excluded", "Some content"), List.of("Java"));
        postService.createPost(new CreatePostDTO("Included", "Some search result"), List.of("Java"));

        List<PostResponseDTO> searchResponse = postService.searchPosts("search");

        assertEquals(2, searchResponse.size());
    }

    @Test
    void searchTest_shouldReturnNoResults() {
        List<PostResponseDTO> searchResponse = postService.searchPosts("search");
        assertEquals(0, searchResponse.size());
    }

    private void seedData(Statement stmt) throws Exception {
        stmt.execute("""
                    INSERT INTO users (id, username, email, password)
                    VALUES (1, 'test', 'test@test.com', 'pass')
                """);

        stmt.execute("""
                    INSERT INTO tags (name) VALUES
                        ('Java'),
                        ('JavaFX'),
                        ('PostgreSQL'),
                        ('SQL'),
                        ('JUnit')
                """);
    }
}