package dtos;

public class CommentResponseDTO {
    private int commentId;
    private String comment;
    private String author;
    private String commentedAt;

    public CommentResponseDTO() {
    }

    public CommentResponseDTO(int commentId, String comment, String author, String commentedAt) {
        this.commentId = commentId;
        this.comment = comment;
        this.author = author;
        this.commentedAt = commentedAt;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(String commentedAt) {
        this.commentedAt = commentedAt;
    }
}
