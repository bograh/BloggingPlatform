package services;

import dao.UserDAO;
import dtos.request.CreateUserDTO;
import dtos.response.UserResponseDTO;
import exceptions.UserExistsException;
import models.User;
import utils.UserUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String registerUser(CreateUserDTO createUserDTO) {

        User user = new User(
                0,
                createUserDTO.getUsername(),
                createUserDTO.getEmail(),
                createUserDTO.getPassword(),
                LocalDateTime.now()
        );

        try {
            userDAO.addUser(user);
            System.out.println("User created successfully!!");
            return "User created successfully!!";
        } catch (UserExistsException | SQLException e) {
            System.out.printf("Error creating user: %s\n", e.getMessage());
            return String.format("Error creating user: %s\n", e.getMessage());
        }
    }

    public User signInUser(String email, String password) {
        try {
            UserResponseDTO userResponse = userDAO.getUserByEmailAndPassword(email, password);
            if (userResponse == null) {
                System.out.println("Invalid email or password");
                return null;
            }
            System.out.println(userResponse);
            return new User(
                    userResponse.getUserId(),
                    userResponse.getUsername(),
                    userResponse.getEmail(),
                    null,
                    UserUtils.convertFormattedDateToISO(userResponse.getCreatedAt())
            );
        } catch (SQLException e) {
            System.out.printf("Invalid email or password: %s\n", e.getMessage());
        }
        return null;
    }

    public void updateUser() {

    }

}