package utils;

import dtos.response.PostResponseDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostUtils {
    public PostResponseDTO mapRowToPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String body = rs.getString("body");
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        String author = rs.getString("author");

        return new PostResponseDTO(
                id,
                title,
                body,
                author,
                updatedAt.format(DateTimeFormatter.BASIC_ISO_DATE)
        );
    }
}
