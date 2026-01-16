package ui;

import controllers.UserController;
import dtos.request.CreateUserDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.User;

public class LoginViewController {
    private final BloggingApp app;
    private final UserController userController;
    private VBox view;

    public LoginViewController(BloggingApp app, UserController userController) {
        this.app = app;
        this.userController = userController;
        initializeView();
    }

    private void initializeView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(20));

        Label titleLabel = new Label("BloggingApp - Smart Blogging Platform");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(350);

        Tab loginTab = new Tab("Login");
        loginTab.setClosable(false);
        loginTab.setContent(createLoginForm());

        Tab registerTab = new Tab("Register");
        registerTab.setClosable(false);
        registerTab.setContent(createRegisterForm());

        tabPane.getTabs().addAll(loginTab, registerTab);

        view.getChildren().addAll(titleLabel, tabPane);
    }

    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter email and password");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            User user = userController.signInUser(email, password);
            if (user != null) {
                app.onLoginSuccess(user);
            } else {
                messageLabel.setText("Invalid email or password");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(messageLabel, 0, 3, 2, 1);

        return grid;
    }

    private GridPane createRegisterForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button registerButton = new Button("Register");
        registerButton.setDefaultButton(true);
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("All fields are required");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            CreateUserDTO userDTO = new CreateUserDTO();
            userDTO.setUsername(username);
            userDTO.setEmail(email);
            userDTO.setPassword(password);

            String response = userController.registerUser(userDTO);

            if (response.toLowerCase().contains("successfully")) {
                messageLabel.setText(response);
                messageLabel.setStyle("-fx-text-fill: green;");
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
            } else {
                messageLabel.setText(response);
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(registerButton, 1, 3);
        grid.add(messageLabel, 0, 4, 2, 1);

        return grid;
    }

    public VBox getView() {
        return view;
    }
}