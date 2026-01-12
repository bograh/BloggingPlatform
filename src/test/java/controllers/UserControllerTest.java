package controllers;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dao.UserDAO;
import dtos.request.CreateUserDTO;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.UserService;
import utils.RandomUserGenerator;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() throws SQLException {
        H2ConnectionProvider connectionProvider = new H2ConnectionProvider();
        TestDatabaseSetup.reset(connectionProvider);

        UserDAO userDAO = new UserDAO(connectionProvider);
        userService = new UserService(userDAO);
        userController = new UserController(userService);
    }

    @Test
    void registerUser_shouldCreateUser() {
        CreateUserDTO createUserDTO = RandomUserGenerator.randomUser();
        String response = userController.registerUser(createUserDTO);
        assertTrue(response.toLowerCase().contains("successfully"));
    }

    @Test
    void registerUser_shouldFailToCreateUser() {
        CreateUserDTO createUserDTO = new CreateUserDTO("ben", "ben@email.com", "pass");
        String response = userController.registerUser(createUserDTO);
        assertTrue(response.toLowerCase().contains("successfully"));

        String unsuccessfulResponse = userController.registerUser(createUserDTO);
        assertTrue(unsuccessfulResponse.toLowerCase().contains("conflict"));
    }

    @Test
    void signInUser_returnsUserIfExists() {
        CreateUserDTO dto = new CreateUserDTO("ben", "ben@email.com", "pass");
        userService.registerUser(dto);

        User result = userController.signInUser(dto.getEmail(), dto.getPassword());

        assertNotNull(result);
        assertEquals("ben@email.com", result.getEmail());
        assertEquals("ben", result.getUsername());
    }

    @Test
    void signInUser_returnsNullIfUserDoesNotExist() {
        User result = userController.signInUser("test@email.com", "pass");
        assertNull(result, "User should be null if not registered");
    }

}