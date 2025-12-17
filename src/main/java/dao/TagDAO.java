package dao;

import models.Tag;
import utils.TagUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    public void addTag(String name) throws SQLException {
        String query = "INSERT into tags (name) VALUES (?)";
        try (Connection conn = Database.getConnection();
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

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tags.add(tagUtils.mapRowToTag(rs));
            }
        }
        return tags;
    }


}
