package controllers;

import dtos.request.CreateCommentDTO;
import dtos.response.CommentResponseDTO;
import services.CommentService;

import java.util.List;

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    public String addCommentToPost(CreateCommentDTO createCommentDTO) {
        return commentService.addCommentToPost();
    }

    public List<CommentResponseDTO> getAllCommentsByPostId(int postId) {
        return commentService.getAllCommentsByPostId(postId);
    }

    public CommentResponseDTO getCommentById(int commentId) {
        return commentService.getCommentById(commentId);
    }

    public String deleteComment(int commentId) {
        return commentService.deleteComment(commentId);
    }

}
