package dao;

import config.ConnectionProvider;
import models.Tag;
import utils.TagUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagDAO {

    private final ConnectionProvider connectionProvider;

    public TagDAO(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }

    public void addTag(String name) throws SQLException {
        String query = "INSERT into tags (name) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
            }
        }
    }

    public List<Tag> getAllTags() throws SQLException {
        String query = "SELECT * FROM tags";
        List<Tag> tags = new ArrayList<>();
        TagUtils tagUtils = new TagUtils();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tags.add(tagUtils.mapRowToTag(rs));
            }
        }
        return tags;
    }

    public List<Tag> getAllTagsFromList(List<String> tagsList) throws SQLException {
        if (tagsList == null || tagsList.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(",", Collections.nCopies(tagsList.size(), "?"));
        String query = "SELECT * FROM tags WHERE name IN (" + placeholders + ")";

        List<Tag> tags = new ArrayList<>();
        TagUtils tagUtils = new TagUtils();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < tagsList.size(); i++) {
                stmt.setString(i + 1, tagsList.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(tagUtils.mapRowToTag(rs));
                }
            }
        }
        return tags;
    }


}
