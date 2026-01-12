package controllers;

import dtos.request.CreateUserDTO;
import models.User;
import services.UserService;
import utils.QueryTimingLogger;

import java.time.Instant;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public String registerUser(CreateUserDTO createUserDTO) {
        Instant start = Instant.now();
        String response = userService.registerUser(createUserDTO);
        QueryTimingLogger.log("registerUser", start, Instant.now());
        return response;
    }

    public User signInUser(String email, String password) {
        Instant start = Instant.now();
        User user = userService.signInUser(email, password);
        QueryTimingLogger.log("signInUser", start, Instant.now());
        return user;
    }
}