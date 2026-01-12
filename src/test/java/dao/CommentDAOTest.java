package dao;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dtos.response.CommentResponseDTO;
import exceptions.CommentNotFoundException;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentDAOTest {

    private CommentDAO commentDAO;

    private int userId;
    private int postId;

    @BeforeEach
    void setup() throws SQLException {
        H2ConnectionProvider provider = new H2ConnectionProvider();
        commentDAO = new CommentDAO(provider);

        TestDatabaseSetup.reset(provider);

        try (Connection conn = provider.getConnection()) {
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, email, password) VALUES ('john', 'john@email.com', 'password')",
                    Statement.RETURN_GENERATED_KEYS
            );
            userStmt.executeUpdate();
            ResultSet userKeys = userStmt.getGeneratedKeys();
            userKeys.next();
            userId = userKeys.getInt(1);

            PreparedStatement postStmt = conn.prepareStatement(
                    "INSERT INTO posts (title, body, author_id) VALUES ('Post', 'Body', ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            postStmt.setInt(1, userId);
            postStmt.executeUpdate();
            ResultSet postKeys = postStmt.getGeneratedKeys();
            postKeys.next();
            postId = postKeys.getInt(1);
        }
    }

    @Test
    void addComment_shouldPersistAndSetId() throws SQLException {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setContent("Nice post!");

        commentDAO.addComment(comment);
        assertTrue(comment.getId() > 0);

        CommentResponseDTO persisted = commentDAO.getCommentById(comment.getId());

        assertNotNull(persisted);
        assertEquals("john", persisted.getAuthor());
        assertEquals("Nice post!", persisted.getComment());


    }

    @Test
    void getAllCommentsByPostId_shouldReturnComments() throws SQLException {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setContent("First comment");
        commentDAO.addComment(comment);

        List<CommentResponseDTO> comments =
                commentDAO.getAllCommentsByPostId(postId);

        assertFalse(comments.isEmpty());
        assertEquals("First comment", comments.getFirst().getComment());
    }

    @Test
    void getAllCommentsByPostId_shouldThrowIfPostNotFound() {
        assertThrows(PostNotFoundException.class, () ->
                commentDAO.getAllCommentsByPostId(9999)
        );
    }

    @Test
    void getCommentById_shouldReturnComment() throws SQLException {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setContent("Hello");

        commentDAO.addComment(comment);

        CommentResponseDTO response =
                commentDAO.getCommentById(comment.getId());

        assertEquals("Hello", response.getComment());
    }

    @Test
    void getCommentById_shouldThrowIfNotFound() {
        assertThrows(CommentNotFoundException.class, () ->
                commentDAO.getCommentById(9999)
        );
    }

    @Test
    void deleteComment_shouldDeleteIfOwner() throws SQLException {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setContent("To delete");

        commentDAO.addComment(comment);

        assertDoesNotThrow(() ->
                commentDAO.deleteComment(comment.getId(), userId)
        );
    }

    @Test
    void deleteComment_shouldThrowForbiddenIfNotOwner() throws SQLException {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setContent("Protected");

        commentDAO.addComment(comment);

        assertThrows(ForbiddenException.class, () ->
                commentDAO.deleteComment(comment.getId(), userId + 1)
        );
    }


}
