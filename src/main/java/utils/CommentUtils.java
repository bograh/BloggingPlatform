package utils;

import dtos.response.CommentResponseDTO;
import models.CommentDocument;
import org.bson.Document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    public CommentDocument mapDocumentToComment(Document document) {
        return new CommentDocument(
                document.getObjectId("_id").toHexString(),
                document.getInteger("postId"),
                document.getString("author"),
                document.getString("content"),
                formatCommentedAt(document.getDate("commentedAt"))
        );
    }

    private String formatCommentedAt(Date date) {
        if (date == null) return null;
        LocalDateTime commentedAt = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return commentedAt.format(DateTimeFormatter.ofPattern(Constants.DateTimeFormatPattern));
    }
}
