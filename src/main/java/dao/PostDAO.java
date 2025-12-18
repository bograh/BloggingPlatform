package dao;

import dtos.response.PostResponseDTO;
import models.Post;
import utils.PostUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public void addPost(Post post, List<Integer> tagIds) throws SQLException {
        String postSql = "INSERT INTO posts (title, body, author_id) VALUES (?, ?, ?)";
        String tagSql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            int postId;
            try (PreparedStatement ps = conn.prepareStatement(postSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, post.getTitle());
                ps.setString(2, post.getBody());
                ps.setInt(3, post.getAuthorId());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("no id returned");
                    postId = rs.getInt(1);
                }
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(tagSql)) {
                    for (int tagId : tagIds) {
                        ps.setInt(1, postId);
                        ps.setInt(2, tagId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            conn.commit();
            post.setId(postId);
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
