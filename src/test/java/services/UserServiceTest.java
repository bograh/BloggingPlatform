package services;

import config.H2ConnectionProvider;
import dao.UserDAO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setup() throws Exception {
        H2ConnectionProvider provider = new H2ConnectionProvider();
        UserDAO userDAO = new UserDAO(provider);

        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    DROP TABLE IF EXISTS users;
                    """);

            stmt.execute("""
                    CREATE TABLE users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255),
                        email VARCHAR(255),
                        password VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }

        userService = new UserService(userDAO);
    }

    @Test
    void registerUser_shouldCreateUser() {
        assertDoesNotThrow(() -> userService.registerUser());
    }

    @Test
    void signInUser_shouldReturnUser() {
        userService.registerUser();

        User user = userService.signInUser();

        assertNotNull(user);
        assertEquals("test", user.getUsername());
    }
}
