package dao;

import dtos.PostResponseDTO;
import models.Post;
import utils.PostUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public void addPost(Post post, int authorId) throws SQLException {
        String query = "INSERT INTO posts (title, body, author_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getBody());
            stmt.setInt(3, authorId);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<PostResponseDTO> getAllPosts() throws SQLException {
        List<PostResponseDTO> posts = new ArrayList<>();
        PostUtils postUtils = new PostUtils();
        String query = """
                SELECT p.id, p.title, p.body, p.updated_at, u.username AS author
                FROM posts p
                         JOIN users u ON u.id = p.author_id
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(postUtils.mapRowToPost(rs));
            }
        }
        return posts;
    }

    public PostResponseDTO getPostById(int id) throws SQLException {
        PostUtils postUtils = new PostUtils();
        String query = """
                SELECT p.id, p.title, p.body, p.updated_at, u.username AS author
                FROM posts p
                         JOIN users u ON u.id = p.author_id
                WHERE p.id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return postUtils.mapRowToPost(rs);
            }
        }
        return null;
    }

    public void updatePost(Post post) throws SQLException {
        String query = "UPDATE posts SET title=?, body=?, updated_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getBody());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, post.getId());
            stmt.executeUpdate();
        }
    }

    public void deletePost(int id) throws SQLException {
        String query = "DELETE FROM posts WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}
