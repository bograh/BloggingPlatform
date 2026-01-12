package ui;

import config.MongoConnection;
import config.PostgresConnectionProvider;
import controllers.CommentController;
import controllers.PostController;
import controllers.UserController;
import dao.CommentDAO;
import dao.PostDAO;
import dao.TagDAO;
import dao.UserDAO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;
import services.CommentService;
import services.PostService;
import services.UserService;
import utils.Constants;

import java.util.Objects;

public class BloggingApp extends Application {

    private Stage primaryStage;
    private User currentUser;

    private UserController userController;
    private PostController postController;
    private CommentController commentController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BloggingApp - Smart Blogging Platform");

        initializeServices();

        showLoginView();

        primaryStage.show();
    }

    private void initializeServices() {
        PostgresConnectionProvider connectionProvider = new PostgresConnectionProvider();
        UserDAO userDAO = new UserDAO(connectionProvider);
        UserService userService = new UserService(userDAO);
        userController = new UserController(userService);
    }

    private void showLoginView() {
        LoginViewController loginController = new LoginViewController(this, userController);
        Scene scene = new Scene(loginController.getView(), 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/application.css")).toExternalForm());
        primaryStage.setScene(scene);
    }

    public void onLoginSuccess(User user) {
        this.currentUser = user;

        PostgresConnectionProvider connectionProvider = new PostgresConnectionProvider();
        PostDAO postDAO = new PostDAO(connectionProvider);
        TagDAO tagDAO = new TagDAO(connectionProvider);
        CommentDAO commentDAO = new CommentDAO(
                MongoConnection.getDatabase(),
                Constants.CommentsMongoCollection
        );

        PostService postService = new PostService(currentUser, postDAO, tagDAO);
        CommentService commentService = new CommentService(currentUser, commentDAO);

        postController = new PostController(postService);
        commentController = new CommentController(commentService);

        showMainView();
    }

    private void showMainView() {
        MainViewController mainController = new MainViewController(this, postController, commentController, currentUser);
        Scene scene = new Scene(mainController.getView(), 1000, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/application.css")).toExternalForm());
        primaryStage.setScene(scene);
    }

    public void showSearchView() {
        SearchViewController searchController = new SearchViewController(this, postController);
        Scene scene = new Scene(searchController.getView(), 900, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/application.css")).toExternalForm());
        primaryStage.setScene(scene);
    }

    public User getCurrentUser() {
        return currentUser;
    }


}