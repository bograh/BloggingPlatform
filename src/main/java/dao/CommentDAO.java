package dao;

import config.ConnectionProvider;
import dtos.response.CommentResponseDTO;
import exceptions.CommentNotFoundException;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Comment;
import utils.CommentUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    private final ConnectionProvider connectionProvider;

    public CommentDAO(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }

    public void addComment(Comment comment) throws SQLException {
        String query = "INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, comment.getPostId());
            stmt.setInt(2, comment.getAuthorId());
            stmt.setString(3, comment.getContent());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<CommentResponseDTO> getAllCommentsByPostId(int postId) throws SQLException {
        if (!postExists(postId)) {
            throw new PostNotFoundException(
                    "NotFound - Post with id: " + postId + " not found."
            );
        }

        List<CommentResponseDTO> comments = new ArrayList<>();
        CommentUtils commentUtils = new CommentUtils();

        String query = """
                SELECT c.id, c.content, u.username AS author, c.commented_at
                FROM comments c
                JOIN users u ON u.id = c.author_id
                WHERE c.post_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(commentUtils.mapRowToComment(rs));
                }
            }
        }
        return comments;
    }

    public CommentResponseDTO getCommentById(int id) throws SQLException {
        CommentUtils commentUtils = new CommentUtils();
        String query = """
                SELECT c.id, c.content, u.username AS author, c.commented_at
                FROM comments c
                JOIN users u ON u.id = c.author_id
                WHERE c.id = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return commentUtils.mapRowToComment(rs);
            }
        }
        throw new CommentNotFoundException("NotFound - Comment with id: " + id + " not found.");
    }

    public void deleteComment(int id, int signedInUserId) throws SQLException {
        String query = "DELETE FROM comment WHERE id=? AND author_id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setInt(2, signedInUserId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ForbiddenException("Forbidden - You are not permitted to delete this comment");
            }
        }
    }

    private boolean postExists(int postId) throws SQLException {
        String sql = "SELECT 1 FROM posts WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
