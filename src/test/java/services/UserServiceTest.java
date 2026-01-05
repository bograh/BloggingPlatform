package services;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dao.UserDAO;
import dtos.request.CreateUserDTO;
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
        assertDoesNotThrow(() -> userService.registerUser(
                new CreateUserDTO("test", "test@email.com", "password"))
        );
    }

    @Test
    void signInUser_shouldReturnUser() {
        CreateUserDTO createUserDTO = new CreateUserDTO(
                "test", "test@email.com", "password"
        );

        userService.registerUser(createUserDTO);

        User user = userService.signInUser(
                createUserDTO.getEmail(),
                createUserDTO.getPassword()
        );

        assertNotNull(user);
        assertEquals("test", user.getUsername());
    }
}
