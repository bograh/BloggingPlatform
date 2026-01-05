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

public class CommentService {

    private final User user;
    private final CommentDAO commentDAO;

    public CommentService(User user, CommentDAO commentDAO) {
        this.user = user;
        this.commentDAO = commentDAO;
    }

    public String addCommentToPost() {
        CreateCommentDTO newComment = new CreateCommentDTO(
                "Good read",
                2
        );

        Comment comment = new Comment(
                0,
                newComment.getPostId(),
                user.getId(),
                newComment.getCommentContent(),
                LocalDateTime.now()
        );

        try {
            commentDAO.addComment(comment);
            return "Comment added to post successfully!!";
        } catch (SQLException e) {
            System.out.printf("Error occurred while creating comment: %s", e.getMessage());
        }
        return "An error occurred while creating comment";
    }

    public List<CommentResponseDTO> getAllCommentsByPostId(int postId) {
        try {
            return commentDAO.getAllCommentsByPostId(postId);
        } catch (PostNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving comment for post with id: %d\n%s", postId, e.getMessage());
        }
        return new ArrayList<>();
    }

    public CommentResponseDTO getCommentById(int commentId) {
        try {
            return commentDAO.getCommentById(commentId);
        } catch (CommentNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.printf("An error occurred while retrieving comment with id: %d\n%s", commentId, e.getMessage());
        }
        return null;
    }

    public String deleteComment(int commentId) {
        try {
            commentDAO.deleteComment(commentId, user.getId());
            return String.format("Comment with id: %d deleted successfully!", commentId);
        } catch (ForbiddenException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (SQLException e) {
            System.out.printf("An error occurred while deleting comment with id: %d\n%s", commentId, e.getMessage());
        }
        return String.format("An error occurred while deleting comment with id: %d", commentId);
    }

}
