package controllers;

import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import services.PostService;
import utils.QueryTimingLogger;

import java.time.Instant;
import java.util.List;

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public String createPost(CreatePostDTO createPostDTO, List<String> tags) {
        Instant start = Instant.now();
        String response = postService.createPost(createPostDTO, tags);
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

    public String updatePost(UpdatePostDTO updatedPost) {
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

    public List<PostResponseDTO> searchPosts(String query) {
        Instant start = Instant.now();
        List<PostResponseDTO> posts = postService.searchPosts(query);
        QueryTimingLogger.log("getAllPosts", start, Instant.now());
        return posts;
    }
}