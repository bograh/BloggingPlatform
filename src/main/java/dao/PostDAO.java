package dao;

import config.ConnectionProvider;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Post;
import utils.PostUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    private final ConnectionProvider connectionProvider;

    public PostDAO(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }

    public void addPost(Post post, List<Integer> tagIds) throws SQLException {
        String postSql = "INSERT INTO posts (title, body, author_id) VALUES (?, ?, ?)";
        String tagSql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
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
        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return postUtils.mapRowToPost(rs);
            }
        }
        throw new PostNotFoundException("NotFound - Post with id: " + id + " not found.");
    }


    public void updatePost(UpdatePostDTO post, int signedInUserId) throws SQLException {
        String query = "UPDATE posts SET title=?, body=?, updated_at=? WHERE id=? AND author_id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getBody());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, post.getId());
            stmt.setInt(5, signedInUserId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ForbiddenException("Forbidden - You are not permitted to update this post");
            }
        }
    }


    public void deletePost(int id, int signedInUserId) throws SQLException {
        String query = "DELETE FROM posts WHERE id=? AND author_id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setInt(2, signedInUserId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ForbiddenException("Forbidden - You are not permitted to delete this post");
            }
        }
    }

}
