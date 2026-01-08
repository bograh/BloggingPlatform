package services;

import dao.CommentDAO;
import dtos.request.CreateCommentDTO;
import dtos.response.CommentResponseDTO;
import exceptions.CommentNotFoundException;
import exceptions.ForbiddenException;
import exceptions.PostNotFoundException;
import models.Comment;
import models.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentService {

    private final User user;
    private final CommentDAO commentDAO;

    private final CacheService<Integer, CommentResponseDTO> commentCache = new CacheService<>(300_000);
    private final CacheService<String, List<CommentResponseDTO>> commentsListCache = new CacheService<>(300_000);

    public CommentService(User user, CommentDAO commentDAO) {
        this.user = user;
        this.commentDAO = commentDAO;
    }

    public String addCommentToPost(CreateCommentDTO newComment) {
        Comment comment = new Comment(
                0,
                newComment.getPostId(),
                user.getId(),
                newComment.getCommentContent(),
                LocalDateTime.now()
        );

        try {
            commentDAO.addComment(comment);

            commentsListCache.invalidate("post_" + newComment.getPostId());

            return "Comment added to post successfully!!";
        } catch (SQLException e) {
            System.out.printf("Error occurred while creating comment: %s\n", e.getMessage());
        }
        return "An error occurred while creating comment";
    }

    public List<CommentResponseDTO> getAllCommentsByPostId(int postId) {
        String cacheKey = "post_" + postId;

        Optional<List<CommentResponseDTO>> cachedComments = commentsListCache.get(cacheKey);
        if (cachedComments.isPresent()) {
            System.out.println("[CACHE HIT] Retrieved comments for post " + postId + " from cache");
            return cachedComments.get();
        }

        System.out.println("[CACHE MISS] Fetching comments for post " + postId + " from database");
        try {
            List<CommentResponseDTO> comments = commentDAO.getAllCommentsByPostId(postId);
            commentsListCache.set(cacheKey, comments);
            return comments;
        } catch (PostNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving comment for post with id: %d\n%s\n", postId, e.getMessage());
        }
        return new ArrayList<>();
    }

    public CommentResponseDTO getCommentById(int commentId) {
        Optional<CommentResponseDTO> cachedComment = commentCache.get(commentId);
        if (cachedComment.isPresent()) {
            System.out.println("[CACHE HIT] Retrieved comment " + commentId + " from cache");
            return cachedComment.get();
        }

        System.out.println("[CACHE MISS] Fetching comment " + commentId + " from database");
        try {
            CommentResponseDTO comment = commentDAO.getCommentById(commentId);
            commentCache.set(commentId, comment);
            return comment;
        } catch (CommentNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving comment with id: %d\n%s\n", commentId, e.getMessage());
        }
        return null;
    }

    public String deleteComment(int commentId) {
        try {
            commentDAO.deleteComment(commentId, user.getId());

            commentCache.invalidate(commentId);
            commentsListCache.clear();

            return String.format("Comment with id: %d deleted successfully!", commentId);
        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (SQLException e) {
            System.out.printf("An error occurred while deleting comment with id: %d\n%s\n", commentId, e.getMessage());
        }
        return String.format("An error occurred while deleting comment with id: %d", commentId);
    }

    public String getCacheStatistics() {
        return "Comment Cache: " + commentCache.getStatistics() + "\n" +
                "Comment List Cache: " + commentsListCache.getStatistics();
    }

    public void clearAllCaches() {
        commentCache.clear();
        commentsListCache.clear();
    }

}
