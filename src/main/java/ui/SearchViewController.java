package ui;

import controllers.PostController;
import dtos.response.PostResponseDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import utils.SortingUtils;

import java.util.List;

public class SearchViewController {
    private final BloggingApp app;
    private final PostController postcontroller;
    private BorderPane view;
    private VBox resultsContainer;
    private Label statusLabel;
    private List<PostResponseDTO> currentResults;

    public SearchViewController(BloggingApp app, PostController postcontroller) {
        this.app = app;
        this.postcontroller = postcontroller;
        initializeView();
    }

    private void initializeView() {
        view = new BorderPane();

        // Top: Search Form
        VBox topSection = new VBox(15);
        topSection.setPadding(new Insets(20));
        topSection.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Search Posts");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Search by keyword
        HBox keywordBox = new HBox(10);
        keywordBox.setAlignment(Pos.CENTER_LEFT);
        Label keywordLabel = new Label("Keyword:");
        keywordLabel.setPrefWidth(80);
        TextField keywordField = new TextField();
        keywordField.setPromptText("Search in title and content...");
        keywordField.setPrefWidth(300);
        Button keywordButton = new Button("Search");
        keywordButton.setOnAction(e -> searchByKeyword(keywordField.getText()));
        keywordBox.getChildren().addAll(keywordLabel, keywordField, keywordButton);

        // Search by tag
        HBox tagBox = new HBox(10);
        tagBox.setAlignment(Pos.CENTER_LEFT);
        Label tagLabel = new Label("Tag:");
        tagLabel.setPrefWidth(80);
        TextField tagField = new TextField();
        tagField.setPromptText("Enter tag name...");
        tagField.setPrefWidth(300);
        Button tagButton = new Button("Search");
        tagButton.setOnAction(e -> searchByTag(tagField.getText()));
        tagBox.getChildren().addAll(tagLabel, tagField, tagButton);

        // Search by author
        HBox authorBox = new HBox(10);
        authorBox.setAlignment(Pos.CENTER_LEFT);
        Label authorLabel = new Label("Author:");
        authorLabel.setPrefWidth(80);
        TextField authorField = new TextField();
        authorField.setPromptText("Enter author username...");
        authorField.setPrefWidth(300);
        Button authorButton = new Button("Search");
        authorButton.setOnAction(e -> searchByAuthor(authorField.getText()));
        authorBox.getChildren().addAll(authorLabel, authorField, authorButton);

        // Sort options
        HBox sortBox = new HBox(10);
        sortBox.setAlignment(Pos.CENTER_LEFT);
        Label sortLabel = new Label("Sort by:");
        sortLabel.setPrefWidth(80);
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Date (Newest)", "Date (Oldest)", "Title (A-Z)", "Title (Z-A)",
                "Author (A-Z)", "Author (Z-A)");
        sortComboBox.setValue("Date (Newest)");
        sortComboBox.setPrefWidth(150);
        sortBox.getChildren().addAll(sortLabel, sortComboBox);

        // Back button
        Button backButton = new Button("Back to Main");
        backButton.setOnAction(e -> app.onLoginSuccess(app.getCurrentUser()));

        topSection.getChildren().addAll(titleLabel, keywordBox, tagBox, authorBox, sortBox, backButton);

        view.setTop(topSection);

        // Center: Results
        ScrollPane scrollPane = new ScrollPane();
        resultsContainer = new VBox(15);
        resultsContainer.setPadding(new Insets(20));
        scrollPane.setContent(resultsContainer);
        scrollPane.setFitToWidth(true);
        view.setCenter(scrollPane);

        // Bottom: Status
        statusLabel = new Label("Enter a search term to begin");
        statusLabel.setPadding(new Insets(5));
        statusLabel.setStyle("-fx-background-color: #f0f0f0;");
        view.setBottom(statusLabel);

        // Set sort combo box action
        sortComboBox.setOnAction(e -> {
            String selected = sortComboBox.getValue();
            if (!resultsContainer.getChildren().isEmpty()) {
                sortCurrentResults(selected);
            }
        });
    }

    private void searchByKeyword(String keyword) {
        if (keyword.trim().isEmpty()) {
            showAlert("Please enter a search keyword");
            return;
        }

        currentResults = postcontroller.searchPosts(keyword);
        displayResults(currentResults, "keyword: " + keyword);
    }

    private void searchByTag(String tag) {
        if (tag.trim().isEmpty()) {
            showAlert("Please enter a tag name");
            return;
        }

        currentResults = postcontroller.searchPostsByTag(tag);
        displayResults(currentResults, "tag: " + tag);
    }

    private void searchByAuthor(String author) {
        if (author.trim().isEmpty()) {
            showAlert("Please enter an author username");
            return;
        }

        currentResults = postcontroller.searchPostsByAuthor(author);
        displayResults(currentResults, "author: " + author);
    }

    private void displayResults(List<PostResponseDTO> results, String searchCriteria) {
        resultsContainer.getChildren().clear();

        if (results.isEmpty()) {
            Label emptyLabel = new Label("No results found for " + searchCriteria);
            emptyLabel.setFont(Font.font(14));
            resultsContainer.getChildren().add(emptyLabel);
            statusLabel.setText("No results found");
        } else {
            for (PostResponseDTO post : results) {
                resultsContainer.getChildren().add(createResultCard(post));
            }
            statusLabel.setText(String.format("Found %d post(s) for %s", results.size(), searchCriteria));
        }
    }

    private VBox createResultCard(PostResponseDTO post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: white;");

        Label titleLabel = new Label(post.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setWrapText(true);

        Label metaLabel = new Label(String.format("By: %s | Posted: %s",
                post.getAuthor(), post.getLastUpdated()));
        metaLabel.setFont(Font.font(12));
        metaLabel.setStyle("-fx-text-fill: #666666;");

        String bodyPreview = post.getBody().length() > 200
                ? post.getBody().substring(0, 200) + "..."
                : post.getBody();
        Label bodyLabel = new Label(bodyPreview);
        bodyLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, metaLabel, bodyLabel);
        return card;
    }

    private void sortCurrentResults(String sortOption) {
        if (currentResults == null || currentResults.isEmpty()) {
            return;
        }

        switch (sortOption) {
            case "Date (Newest)" -> SortingUtils.sortPosts(currentResults, "date", false);
            case "Date (Oldest)" -> SortingUtils.sortPosts(currentResults, "date", true);
            case "Title (A-Z)" -> SortingUtils.sortPosts(currentResults, "title", true);
            case "Title (Z-A)" -> SortingUtils.sortPosts(currentResults, "title", false);
            case "Author (A-Z)" -> SortingUtils.sortPosts(currentResults, "author", true);
            case "Author (Z-A)" -> SortingUtils.sortPosts(currentResults, "author", false);
        }

        resultsContainer.getChildren().clear();
        for (PostResponseDTO post : currentResults) {
            resultsContainer.getChildren().add(createResultCard(post));
        }
        statusLabel.setText(String.format("Showing %d results (sorted by %s)",
                currentResults.size(), sortOption));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Search");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }
}