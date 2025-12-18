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

    public void createPost() {
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
            System.out.println("Blog post created successfully!!");
        } catch (SQLException e) {
            System.out.printf("Error occurred while creating posts: %s", e.getMessage());
        }

    }

    public void getAllPosts() {
        try {
            List<PostResponseDTO> posts = postDAO.getAllPosts();
            System.out.println(posts);
        } catch (SQLException e) {
            System.out.printf("An error occurred when retrieving posts: %s", e.getMessage());
        }

    }

    public void getPostById(int postId) {
        try {
            PostResponseDTO post = postDAO.getPostById(postId);
            System.out.println(post);
        } catch (PostNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred when retrieving post with id: %d\n%s", postId, e.getMessage());
        }

    }

    /*public void updatePost(Post post) throws SQLException
    public void deletePost(int id) throws SQLException*/

    public void updatePost(int postId) {
        try {
            UpdatePostDTO updatedPost = new UpdatePostDTO(
                    postId,
                    "New Post Title",
                    "New Post Body",
                    LocalDateTime.now()
            );

            postDAO.updatePost(updatedPost, user.getId());
            System.out.printf("Post with id: %d updated successfully!", postId);

        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred when updating post with id: %d\n%s", postId, e.getMessage());
        }

    }

    public void deletePost(int postId) {

    }

}
