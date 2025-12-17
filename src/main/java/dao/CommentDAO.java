package dao;

import dtos.CommentResponseDTO;
import models.Comment;
import utils.CommentUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public void addComment(Comment comment) throws SQLException {
        String query = "INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
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
        List<CommentResponseDTO> comments = new ArrayList<>();
        CommentUtils commentUtils = new CommentUtils();

        String query = """
                SELECT c.id, c.content, u.username AS author, c.commented_at
                FROM comments c
                JOIN users u ON u.id = c.author_id
                WHERE c.post_id = ?
                """;

        try (Connection conn = Database.getConnection();
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
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return commentUtils.mapRowToComment(rs);
            }
        }
        return null;
    }

    public void deleteComment(int id) throws SQLException {
        String query = "DELETE FROM comment WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
