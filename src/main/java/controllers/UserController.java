package controllers;

import models.User;
import services.UserService;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerUser() {
        userService.registerUser();
    }

    public User signInUser() {
        return userService.signInUser();
    }
}
