package ui;

import controllers.CommentController;
import controllers.PostController;
import dtos.request.CreateCommentDTO;
import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.CommentDocument;
import models.User;
import utils.SortingUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewController {
    private final BloggingApp app;
    private final PostController postController;
    private final CommentController commentController;
    private final User currentUser;
    private BorderPane view;
    private VBox postsContainer;
    private Label statusLabel;

    public MainViewController(BloggingApp app, PostController postController, CommentController commentController, User currentUser) {
        this.app = app;
        this.postController = postController;
        this.commentController = commentController;
        this.currentUser = currentUser;
    }

    private void initializeView() {
        view = new BorderPane();

        MenuBar menuBar = createMenuBar();
        view.setTop(menuBar);

        ScrollPane scrollPane = new ScrollPane();
        postsContainer = new VBox(15);
        postsContainer.setPadding(new Insets(20));
        scrollPane.setContent(postsContainer);
        scrollPane.setFitToWidth(true);
        view.setCenter(scrollPane);

        statusLabel = new Label("Welcome, " + currentUser.getUsername() + "!");
        statusLabel.setPadding(new Insets(5));
        statusLabel.setStyle("-fx-background-color: #f0f0f0;");
        view.setBottom(statusLabel);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh Posts");
        refreshItem.setOnAction(e -> loadPosts());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), exitItem);

        Menu postMenu = new Menu("Post");
        MenuItem createPostItem = new MenuItem("Create New Post");
        createPostItem.setOnAction(e -> showCreatePostDialog());
        MenuItem sortByDateItem = new MenuItem("Sort by Date");
        sortByDateItem.setOnAction(e -> sortPosts("date", false));
        MenuItem sortByTitleItem = new MenuItem("Sort by Title");
        sortByTitleItem.setOnAction(e -> sortPosts("title", true));
        MenuItem sortByAuthorItem = new MenuItem("Sort by Author");
        sortByAuthorItem.setOnAction(e -> sortPosts("author", true));
        postMenu.getItems().addAll(createPostItem, new SeparatorMenuItem(),
                sortByDateItem, sortByTitleItem, sortByAuthorItem);

        Menu searchMenu = new Menu("Search");
        MenuItem searchViewItem = new MenuItem("Open Search");
        searchViewItem.setOnAction(e -> app.showSearchView());
        searchMenu.getItems().add(searchViewItem);

        menuBar.getMenus().addAll(fileMenu, postMenu, searchMenu);
        return menuBar;
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();
        List<PostResponseDTO> posts = postController.getAllPosts();

        if (posts.isEmpty()) {
            Label emptyLabel = new Label("No posts available. Create your first post!");
            emptyLabel.setFont(Font.font(14));
            postsContainer.getChildren().add(emptyLabel);
        } else {
            for (PostResponseDTO post : posts) {
                postsContainer.getChildren().add(createPostCard(post));
            }
            statusLabel.setText(String.format("Loaded %d posts", posts.size()));
        }
    }

    private VBox createPostCard(PostResponseDTO post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: white;");

        Label titleLabel = new Label(post.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setWrapText(true);

        Label metaLabel = new Label(String.format("By: %s | Posted: %s",
                post.getAuthor(),
                post.getLastUpdated()));
        metaLabel.setFont(Font.font(12));
        metaLabel.setStyle("-fx-text-fill: #666666;");

        String bodyPreview = post.getBody().length() > 200
                ? post.getBody().substring(0, 200) + "..."
                : post.getBody();
        Label bodyLabel = new Label(bodyPreview);
        bodyLabel.setWrapText(true);

        HBox buttonBox = new HBox(10);
        Button viewButton = new Button("View Details");
        viewButton.setOnAction(e -> showPostDetails(post));

        buttonBox.getChildren().add(viewButton);

        if (post.getAuthor().equals(currentUser.getUsername())) {
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> showEditPostDialog(post));
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
            deleteButton.setOnAction(e -> deletePost(post.getPostId()));
            buttonBox.getChildren().addAll(editButton, deleteButton);
        }

        card.getChildren().addAll(titleLabel, metaLabel, bodyLabel, buttonBox);
        return card;
    }

    private void showPostDetails(PostResponseDTO post) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Post Details");
        dialog.setHeaderText(post.getTitle());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);

        Label metaLabel = new Label(String.format("By: %s | Posted: %s",
                post.getAuthor(), post.getLastUpdated()));

        TextArea bodyArea = new TextArea(post.getBody());
        bodyArea.setWrapText(true);
        bodyArea.setEditable(false);
        bodyArea.setPrefRowCount(10);

        Label commentsHeader = new Label("Comments:");
        commentsHeader.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox commentsBox = new VBox(10);
        List<CommentDocument> comments = commentController.getAllCommentsByPostId(post.getPostId());

        if (comments.isEmpty()) {
            commentsBox.getChildren().add(new Label("No comments yet."));
        } else {
            for (CommentDocument comment : comments) {
                Label commentLabel = new Label(String.format("%s: %s",
                        comment.getAuthor(),
                        comment.getContent()));
                commentLabel.setWrapText(true);
                commentsBox.getChildren().add(commentLabel);
            }
        }

        Label addCommentLabel = new Label("Add Comment:");
        addCommentLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Enter your comment...");
        commentArea.setPrefRowCount(3);

        Button addCommentButton = new Button("Post Comment");
        addCommentButton.setOnAction(e -> {
            String commentText = commentArea.getText().trim();
            if (!commentText.isEmpty()) {
                CreateCommentDTO commentDTO = new CreateCommentDTO(commentText, post.getPostId());
                String response = commentController.addCommentToPost(commentDTO);

                if (response.contains("successfully")) {
                    commentArea.clear();
                    dialog.close();
                    showAlert("Success", response, Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", response, Alert.AlertType.ERROR);
                }
            }
        });

        content.getChildren().addAll(metaLabel, bodyArea, commentsHeader, commentsBox,
                addCommentLabel, commentArea, addCommentButton);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showCreatePostDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Post");
        dialog.setHeaderText("Create a new blog post");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Post Title");
        titleField.setPrefWidth(400);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Post Content");
        bodyArea.setPrefRowCount(10);
        bodyArea.setPrefWidth(400);

        TextField tagsField = new TextField();
        tagsField.setPromptText("Tags (comma-separated)");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(bodyArea, 1, 1);
        grid.add(new Label("Tags:"), 0, 2);
        grid.add(tagsField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                CreatePostDTO postDTO = new CreatePostDTO();
                postDTO.setTitle(titleField.getText());
                postDTO.setBody(bodyArea.getText());

                List<String> tags = Arrays.stream(tagsField.getText().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                String response = postController.createPost(postDTO, tags);

                if (response.contains("successfully")) {
                    showAlert("Success", response, Alert.AlertType.INFORMATION);
                    loadPosts();
                } else {
                    showAlert("Error", response, Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showEditPostDialog(PostResponseDTO post) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit your blog post");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField(post.getTitle());
        titleField.setPrefWidth(400);

        TextArea bodyArea = new TextArea(post.getBody());
        bodyArea.setPrefRowCount(10);
        bodyArea.setPrefWidth(400);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(bodyArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.showAndWait().ifPresent(button -> {
            if (button == okButton) {
                UpdatePostDTO updateDTO = new UpdatePostDTO();
                updateDTO.setId(post.getPostId());
                updateDTO.setTitle(titleField.getText());
                updateDTO.setBody(bodyArea.getText());

                String response = postController.updatePost(updateDTO);

                if (response.contains("successfully")) {
                    showAlert("Success", "Post updated successfully!", Alert.AlertType.INFORMATION);
                    loadPosts();
                } else {
                    showAlert("Error", response, Alert.AlertType.ERROR);
                }
            }
        });
    }


    private void deletePost(int postId) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Post");
        confirmAlert.setContentText("Are you sure you want to delete this post?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = postController.deletePost(postId);
                if (result.contains("successfully")) {
                    showAlert("Success", "Post deleted successfully!", Alert.AlertType.INFORMATION);
                    loadPosts();
                } else {
                    showAlert("Error", result, Alert.AlertType.ERROR);
                }
            }
        });
    }


    private void sortPosts(String sortBy, boolean ascending) {
        List<PostResponseDTO> posts = postController.getAllPosts();
        SortingUtils.sortPosts(posts, sortBy, ascending);

        postsContainer.getChildren().clear();
        for (PostResponseDTO post : posts) {
            postsContainer.getChildren().add(createPostCard(post));
        }
        statusLabel.setText(String.format("Sorted %d posts by %s (%s)",
                posts.size(), sortBy, ascending ? "ascending" : "descending"));
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }
}