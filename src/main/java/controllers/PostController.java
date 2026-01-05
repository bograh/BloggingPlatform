package controllers;

import dtos.request.CreatePostDTO;
import dtos.request.UpdatePostDTO;
import dtos.response.PostResponseDTO;
import services.PostService;

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

        return postService.createPost(dto, tagsList);
    }

    public List<PostResponseDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    public PostResponseDTO getPostById(int postId) {
        return postService.getPostById(postId);
    }

    public String updatePost(int postId) {
        UpdatePostDTO updatedPost = new UpdatePostDTO(
                postId,
                "New Post Title",
                "New Post Body",
                LocalDateTime.now()
        );
        return postService.updatePost(updatedPost);
    }

    public String deletePost(int postId) {
        return postService.deletePost(postId);
    }
}
