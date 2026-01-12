package dao;

import config.H2ConnectionProvider;
import config.TestDatabaseSetup;
import dtos.response.UserResponseDTO;
import exceptions.UserExistsException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        H2ConnectionProvider provider = new H2ConnectionProvider();
        TestDatabaseSetup.reset(provider);

        userDAO = new UserDAO(provider);
    }


    @Test
    void addUser_shouldPersistUserAndSetId() throws Exception {
        User user = new User(
                0,
                "john",
                "john@email.com",
                "password",
                LocalDateTime.now()
        );

        userDAO.addUser(user);

        assertTrue(user.getId() > 0);
    }

    @Test
    void addUser_shouldFailDueToConflict() throws Exception {
        User user = new User(
                0,
                "john",
                "john@email.com",
                "password",
                LocalDateTime.now()
        );

        userDAO.addUser(user);
        assertTrue(user.getId() > 0);

        assertThrows(UserExistsException.class, () -> userDAO.addUser(user));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        User user = new User(0, "anna", "anna@email.com", "pass", LocalDateTime.now());
        userDAO.addUser(user);

        UserResponseDTO result = userDAO.getUserById(user.getId());

        assertNotNull(result);
        assertEquals("anna", result.getUsername());
    }

    @Test
    void getUserByEmailAndPassword_shouldReturnUser() throws Exception {
        User user = new User(0, "sam", "sam@email.com", "1234", LocalDateTime.now());
        userDAO.addUser(user);

        UserResponseDTO result =
                userDAO.getUserByEmailAndPassword("sam@email.com", "1234");

        assertNotNull(result);
        assertEquals("sam", result.getUsername());
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        userDAO.addUser(new User(0, "u1", "u1@email.com", "p1", LocalDateTime.now()));
        userDAO.addUser(new User(0, "u2", "u2@email.com", "p2", LocalDateTime.now()));

        List<UserResponseDTO> users = userDAO.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void updateUser_shouldModifyUser() throws Exception {
        User user = new User(0, "old", "old@email.com", "pass", LocalDateTime.now());
        userDAO.addUser(user);

        user.setUsername("new");
        userDAO.updateUser(user);

        UserResponseDTO updated = userDAO.getUserById(user.getId());
        assertEquals("new", updated.getUsername());
    }

    @Test
    void deleteUser_shouldRemoveUser() throws Exception {
        User user = new User(0, "del", "del@email.com", "pass", LocalDateTime.now());
        userDAO.addUser(user);

        userDAO.deleteUser(user.getId());

        assertNull(userDAO.getUserById(user.getId()));
    }
}