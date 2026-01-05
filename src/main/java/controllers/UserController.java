package controllers;

import models.User;
import services.UserService;
import utils.QueryTimingLogger;

import java.time.Instant;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerUser() {
        Instant start = Instant.now();
        userService.registerUser();
        QueryTimingLogger.log("registerUser", start, Instant.now());
    }

    public User signInUser() {
        Instant start = Instant.now();
        User user = userService.signInUser();
        QueryTimingLogger.log("signInUser", start, Instant.now());
        return user;
    }
}
