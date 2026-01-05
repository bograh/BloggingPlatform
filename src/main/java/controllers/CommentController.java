package controllers;

import dtos.request.CreateCommentDTO;
import dtos.response.CommentResponseDTO;
import services.CommentService;
import utils.QueryTimingLogger;

import java.time.Instant;
import java.util.List;

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    public String addCommentToPost(CreateCommentDTO createCommentDTO) {
        Instant start = Instant.now();
        String response = commentService.addCommentToPost();
        QueryTimingLogger.log("addCommentToPost", start, Instant.now());
        return response;
    }

    public List<CommentResponseDTO> getAllCommentsByPostId(int postId) {
        Instant start = Instant.now();
        List<CommentResponseDTO> comments = commentService.getAllCommentsByPostId(postId);
        QueryTimingLogger.log("getAllCommentsForPost", start, Instant.now());
        return comments;
    }

    public CommentResponseDTO getCommentById(int commentId) {
        Instant start = Instant.now();
        CommentResponseDTO comment = commentService.getCommentById(commentId);
        QueryTimingLogger.log("getCommentById", start, Instant.now());
        return comment;
    }

    public String deleteComment(int commentId) {
        Instant start = Instant.now();
        String response = commentService.deleteComment(commentId);
        QueryTimingLogger.log("deleteComment", start, Instant.now());
        return response;
    }

}
