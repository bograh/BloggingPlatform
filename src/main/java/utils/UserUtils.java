package utils;

import dtos.UserResponseDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserUtils {

    public UserResponseDTO mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new UserResponseDTO(
                id, username, email, createdAt.format(DateTimeFormatter.BASIC_ISO_DATE)
        );
    }
}
