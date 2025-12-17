package models;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int postId;
    private int userId;
    private String content;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(int id, int postId, int userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        return "Comment{id=" + id + ", postId=" + postId + ", userId=" + userId +
                ", createdAt=" + createdAt + ", content='" + content + '\'' + '}';
    }
}
