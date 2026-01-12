package dtos.request;

import java.time.LocalDateTime;

public class UpdatePostDTO {
    private int id;
    private String title;
    private String body;
    private LocalDateTime updatedAt;

    public UpdatePostDTO() {
    }

    public UpdatePostDTO(int id, String title, String body, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
