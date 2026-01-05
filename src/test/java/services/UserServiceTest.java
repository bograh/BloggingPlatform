package services;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dao.UserDAO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setup() throws Exception {
        H2ConnectionProvider provider = new H2ConnectionProvider();
        UserDAO userDAO = new UserDAO(provider);
        userService = new UserService(userDAO);

        TestDatabaseSetup.reset(provider);
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
