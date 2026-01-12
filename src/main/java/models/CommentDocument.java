package models;

public class CommentDocument {
    private String id;
    private int postId;
    private String author;
    private String content;
    private String createdAt;

    public CommentDocument() {
    }

    public CommentDocument(String id, int postId, String author, String content, String createdAt) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "\n Comment{id=" + id + ", postId=" + postId + ", author=" + author +
                ", createdAt=" + createdAt + ", content='" + content + '\'' + '}';
    }
}
