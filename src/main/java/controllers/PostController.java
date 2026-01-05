package controllers;

import dtos.request.CreatePostDTO;
import dtos.response.PostResponseDTO;
import services.PostService;

import java.util.List;

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public String createPost(CreatePostDTO createPostDTO) {
        return postService.createPost();
    }

    public List<PostResponseDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    public PostResponseDTO getPostById(int postId) {
        return postService.getPostById(postId);
    }

    public String updatePost(int postId) {
        return postService.updatePost(postId);
    }

    public String deletePost(int postId) {
        return postService.deletePost(postId);
    }
}
