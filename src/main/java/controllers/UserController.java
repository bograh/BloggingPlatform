package controllers;

import dtos.request.CreateUserDTO;
import models.User;
import services.UserService;
import utils.QueryTimingLogger;
import utils.RandomUserGenerator;

import java.time.Instant;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerUser() {
        CreateUserDTO createUserDTO = RandomUserGenerator.randomUser();
        Instant start = Instant.now();
        userService.registerUser(createUserDTO);
        QueryTimingLogger.log("registerUser", start, Instant.now());
    }

    public User signInUser() {
        String email = "ben@email.com";
        String password = "password1";
        Instant start = Instant.now();
        User user = userService.signInUser(email, password);
        QueryTimingLogger.log("signInUser", start, Instant.now());
        return user;
    }
}
