package controllers;

import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import services.PostService;
import utils.QueryTimingLogger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public String createPost(CreatePostDTO createPostDTO) {
        CreatePostDTO dto = new CreatePostDTO(
                "Test Blog Post Title",
                "Test Blog Post Content....."
        );
        List<String> tagsList = new ArrayList<>();
        tagsList.add("Java");
        tagsList.add("JavaFX");
        tagsList.add("PostgreSQL");

        Instant start = Instant.now();
        String response = postService.createPost(dto, tagsList);
        QueryTimingLogger.log("createPost", start, Instant.now());
        return response;
    }

    public List<PostResponseDTO> getAllPosts() {
        Instant start = Instant.now();
        List<PostResponseDTO> posts = postService.getAllPosts();
        QueryTimingLogger.log("getAllPosts", start, Instant.now());
        return posts;
    }

    public PostResponseDTO getPostById(int postId) {
        Instant start = Instant.now();
        PostResponseDTO post = postService.getPostById(postId);
        QueryTimingLogger.log("getPostByID", start, Instant.now());
        return post;
    }

    public String updatePost(int postId) {
        UpdatePostDTO updatedPost = new UpdatePostDTO(
                postId,
                "New Post Title",
                "New Post Body",
                LocalDateTime.now()
        );
        Instant start = Instant.now();
        String response = postService.updatePost(updatedPost);
        QueryTimingLogger.log("updatePost", start, Instant.now());
        return response;
    }

    public String deletePost(int postId) {
        Instant start = Instant.now();
        String response = postService.deletePost(postId);
        QueryTimingLogger.log("deletePost", start, Instant.now());
        return response;
    }
}
