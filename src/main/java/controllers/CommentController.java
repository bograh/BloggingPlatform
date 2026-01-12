package controllers;

import dtos.request.CreateCommentDTO;
import models.CommentDocument;
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
        String response = commentService.addCommentToPost(createCommentDTO);
        QueryTimingLogger.log("addCommentToPost", start, Instant.now());
        return response;
    }

    public List<CommentDocument> getAllCommentsByPostId(int postId) {
        Instant start = Instant.now();
        List<CommentDocument> comments = commentService.getAllCommentsByPostId(postId);
        QueryTimingLogger.log("getAllCommentsForPost", start, Instant.now());
        return comments;
    }

    public CommentDocument getCommentById(String commentId) {
        Instant start = Instant.now();
        CommentDocument comment = commentService.getCommentById(commentId);
        QueryTimingLogger.log("getCommentById", start, Instant.now());
        return comment;
    }

    public String deleteComment(String commentId) {
        Instant start = Instant.now();
        String response = commentService.deleteComment(commentId);
        QueryTimingLogger.log("deleteComment", start, Instant.now());
        return response;
    }

}