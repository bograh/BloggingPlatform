package utils;

import dtos.response.CommentResponseDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentUtils {

    public CommentResponseDTO mapRowToComment(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String content = rs.getString("content");
        String author = rs.getString("author");
        LocalDateTime commentedAt = rs.getTimestamp("commented_at").toLocalDateTime();

        return new CommentResponseDTO(
                id,
                content,
                author,
                commentedAt.format(DateTimeFormatter.ofPattern(Constants.DateTimeFormatPattern))
        );
    }
}
