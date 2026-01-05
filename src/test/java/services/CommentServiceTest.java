package services;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dao.CommentDAO;
import dao.PostDAO;
import dao.TagDAO;
import dao.UserDAO;
import dtos.request.CreateCommentDTO;
import dtos.request.CreatePostDTO;
import dtos.response.CommentResponseDTO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {

    private static final User TEST_USER =
            new User(1, "test", "test@test.com", "pass", null);

    private static final Integer postId = 1;

    private CommentDAO commentDAO;
    private CommentService commentService;

    @BeforeEach
    void setUp() throws Exception {
        H2ConnectionProvider provider = new H2ConnectionProvider();
        commentDAO = new CommentDAO(provider);
        UserDAO userDAO = new UserDAO(provider);
        PostService postService = new PostService(TEST_USER, new PostDAO(provider), new TagDAO(provider));
        commentService = new CommentService(TEST_USER, commentDAO);

        TestDatabaseSetup.reset(provider);

        userDAO.addUser(TEST_USER);
        CreatePostDTO dto = new CreatePostDTO("Sample Post", "Sample Content");
        postService.createPost(dto, List.of("Java"));
    }

    @Test
    void addCommentToPost_shouldReturnSuccessMessage() {
        CreateCommentDTO dto =
                new CreateCommentDTO("Nice article", postId);

        String result = commentService.addCommentToPost(dto);

        assertEquals("Comment added to post successfully!!", result);
    }

    @Test
    void getAllCommentsByPostId_shouldReturnComments() {
        CreateCommentDTO dto =
                new CreateCommentDTO("First comment", postId);

        commentService.addCommentToPost(dto);

        List<CommentResponseDTO> comments =
                commentService.getAllCommentsByPostId(postId);

        assertFalse(comments.isEmpty());
        assertEquals("First comment", comments.getFirst().getComment());
    }

    @Test
    void getAllCommentsByPostId_shouldReturnEmptyListIfPostNotFound() {
        List<CommentResponseDTO> comments =
                commentService.getAllCommentsByPostId(9999);

        assertTrue(comments.isEmpty());
    }

    @Test
    void getCommentById_shouldReturnComment() {
        CreateCommentDTO dto =
                new CreateCommentDTO("Hello", postId);

        commentService.addCommentToPost(dto);

        List<CommentResponseDTO> comments =
                commentService.getAllCommentsByPostId(postId);

        CommentResponseDTO found =
                commentService.getCommentById(comments.getFirst().getCommentId());

        assertNotNull(found);
        assertEquals("Hello", found.getComment());
    }


    @Test
    void getCommentById_shouldReturnNullIfNotFound() {
        CommentResponseDTO result =
                commentService.getCommentById(9999);

        assertNull(result);
    }


    @Test
    void deleteComment_shouldDeleteIfOwner() {
        CreateCommentDTO dto =
                new CreateCommentDTO("To delete", postId);

        commentService.addCommentToPost(dto);

        int commentId =
                commentService.getAllCommentsByPostId(postId).getFirst().getCommentId();

        String result =
                commentService.deleteComment(commentId);

        assertEquals(
                "Comment with id: " + commentId + " deleted successfully!",
                result
        );
    }


    @Test
    void deleteComment_shouldReturnForbiddenMessage() throws SQLException {
        CreateCommentDTO dto =
                new CreateCommentDTO("Protected", postId);

        commentService.addCommentToPost(dto);

        int commentId =
                commentService.getAllCommentsByPostId(1).getFirst().getCommentId();

        User anotherUser = new User(999, "kate", "k@mail.com", "123", LocalDateTime.now());
        CommentService otherUserService =
                new CommentService(anotherUser, commentDAO);

        String result =
                otherUserService.deleteComment(commentId);

        assertTrue(result.contains("Forbidden"));
    }

}
