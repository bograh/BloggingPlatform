package controllers;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dao.PostDAO;
import dao.TagDAO;
import dao.UserDAO;
import dtos.request.CreatePostDTO;
import dtos.request.CreateUserDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.PostService;
import services.UserService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostControllerTest {

    private PostController postController;

    @BeforeEach
    void setUp() throws SQLException {
        H2ConnectionProvider connectionProvider = new H2ConnectionProvider();
        TestDatabaseSetup.reset(connectionProvider);

        UserDAO userDAO = new UserDAO(connectionProvider);
        UserService userService = new UserService(userDAO);
        UserController userController = new UserController(userService);

        String email = "test@email.com";
        String password = "pass";

        CreateUserDTO createUserDTO = new CreateUserDTO("test", email, password);
        userController.registerUser(createUserDTO);
        User user = userController.signInUser(email, password);

        PostDAO postDAO = new PostDAO(connectionProvider);
        TagDAO tagDAO = new TagDAO(connectionProvider);
        PostService postService = new PostService(user, postDAO, tagDAO);
        postController = new PostController(postService);
    }

    @Test
    void createPost_returnsSuccessMessage() {
        CreatePostDTO dto = new CreatePostDTO("Test Title", "Test Content");
        String response = postController.createPost(dto);
        assertEquals("Blog post created successfully!!", response);
    }

    @Test
    void getAllPosts_returnsPosts() {
        postController.createPost(new CreatePostDTO("Title 1", "Content 1"));
        postController.createPost(new CreatePostDTO("Title 2", "Content 2"));

        List<PostResponseDTO> posts = postController.getAllPosts();
        assertEquals(2, posts.size());
    }

    @Test
    void getPostById_returnsCorrectPost() {
        postController.createPost(new CreatePostDTO("Title", "Content"));
        PostResponseDTO createdPost = postController.getAllPosts().getFirst();
        PostResponseDTO fetched = postController.getPostById(createdPost.getPostId());
        assertNotNull(fetched);
        assertEquals("Title", fetched.getTitle());
    }

    @Test
    void updatePost_returnsSuccessMessage() {
        postController.createPost(new CreatePostDTO("Old Title", "Old Body"));
        PostResponseDTO created = postController.getAllPosts().getFirst();

        UpdatePostDTO updateDto = new UpdatePostDTO(created.getPostId(), "New Title", "New Body");
        String response = postController.updatePost(updateDto);

        assertTrue(response.toLowerCase().contains("updated successfully"));
        PostResponseDTO updated = postController.getPostById(created.getPostId());
        assertEquals("New Title", updated.getTitle());
        assertEquals("New Body", updated.getBody());
    }

    @Test
    void deletePost_returnsSuccessMessage() {
        postController.createPost(new CreatePostDTO("Title", "Body"));
        PostResponseDTO created = postController.getAllPosts().getFirst();

        String response = postController.deletePost(created.getPostId());
        String expected = String.format("Post with id: %d deleted successfully!\n", created.getPostId());
        assertEquals(expected, response);

        PostResponseDTO deleted = postController.getPostById(created.getPostId());
        assertNull(deleted);
    }

    @Test
    void deletePost_cannotDeletePost() {
        String response = postController.deletePost(999);
        assertTrue(response.toLowerCase().contains("forbidden"));
    }

    @Test
    void searchTest_returnsTotalResults() {
        postController.createPost(new CreatePostDTO("To Search", "Some content"));
        postController.createPost(new CreatePostDTO("Excluded", "Some content"));
        postController.createPost(new CreatePostDTO("Included", "Some search result"));

        List<PostResponseDTO> searchResponse = postController.searchPosts("search");
        assertEquals(2, searchResponse.size());
    }

    @Test
    void searchTest_returnsNoResults() {
        List<PostResponseDTO> searchResponse = postController.searchPosts("search");
        assertEquals(0, searchResponse.size());
    }


}