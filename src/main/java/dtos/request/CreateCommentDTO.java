package dtos.request;

public class CreateCommentDTO {
    private String commentContent;
    private int postId;

    public CreateCommentDTO(String commentContent, int postId) {
        this.commentContent = commentContent;
        this.postId = postId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
