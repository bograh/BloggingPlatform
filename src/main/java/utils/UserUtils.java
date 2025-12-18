package utils;

import dtos.response.UserResponseDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UserUtils {

    public static LocalDateTime convertFormattedDateToISO(String formattedDate) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(Constants.DateTimeFormatPattern, Locale.ENGLISH);

        return LocalDateTime.parse(
                formattedDate,
                formatter
        );
    }

    public UserResponseDTO mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new UserResponseDTO(
                id,
                username,
                email,
                createdAt.format(DateTimeFormatter.ofPattern(Constants.DateTimeFormatPattern))
        );
    }
}
