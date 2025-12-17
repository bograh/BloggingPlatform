package models;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int postId;
    private int authorId;
    private String content;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(int id, int postId, int authorId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", postId=" + postId + ", userId=" + authorId +
                ", createdAt=" + createdAt + ", content='" + content + '\'' + '}';
    }
}
