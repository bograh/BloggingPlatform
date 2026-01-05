package services;

import dao.PostDAO;
import dao.TagDAO;
import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Post;
import models.Tag;
import models.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    private final User user;
    private final PostDAO postDAO;
    private final TagDAO tagDAO;

    public PostService(User user, PostDAO postDAO, TagDAO tagDAO) {
        this.user = user;
        this.postDAO = postDAO;
        this.tagDAO = tagDAO;
    }

    public String createPost() {
        CreatePostDTO createPostDTO = new CreatePostDTO(
                "Test Blog Post Title",
                "Test Blog Post Content.....",
                user.getId()
        );

        Post post = new Post(
                0,
                createPostDTO.getTitle(),
                createPostDTO.getBody(),
                createPostDTO.getAuthorId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<String> tagsList = new ArrayList<>();
        tagsList.add("Java");
        tagsList.add("JavaFX");
        tagsList.add("PostgreSQL");

        try {
            List<Tag> tags = tagDAO.getAllTagsFromList(tagsList);
            List<Integer> tagIds = new ArrayList<>(tags.stream().map(Tag::getId).toList());
            postDAO.addPost(post, tagIds);
            return "Blog post created successfully!!";
        } catch (SQLException e) {
            System.out.printf("Error occurred while creating post: %s", e.getMessage());
        }
        return "An error occurred while creating post";
    }

    public List<PostResponseDTO> getAllPosts() {
        try {
            return postDAO.getAllPosts();
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving posts: %s", e.getMessage());
        }
        return new ArrayList<>();
    }

    public PostResponseDTO getPostById(int postId) {
        try {
            return postDAO.getPostById(postId);
        } catch (PostNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving post with id: %d\n%s", postId, e.getMessage());
        }
        return null;
    }

    public String updatePost(int postId) {
        try {
            UpdatePostDTO updatedPost = new UpdatePostDTO(
                    postId,
                    "New Post Title",
                    "New Post Body",
                    LocalDateTime.now()
            );

            postDAO.updatePost(updatedPost, user.getId());
            return String.format("Post with id: %d updated successfully!", postId);

        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (SQLException e) {
            System.out.printf("An error occurred while updating post with id: %d\n%s", postId, e.getMessage());
        }
        return String.format("An error occurred while updating post with id: %d", postId);
    }

    public String deletePost(int postId) {
        try {

            postDAO.deletePost(postId, user.getId());
            return String.format("Post with id: %d deleted successfully!\n", postId);

        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (SQLException e) {
            System.out.printf("An error occurred while deleting post with id: %d\n%s", postId, e.getMessage());
        }
        return String.format("An error occurred while deleting post with id: %d", postId);
    }

}
