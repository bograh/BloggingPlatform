package dtos;

public class UserResponseDTO {
    private String username;
    private String email;
    private String createdAt;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String username, String email, String createdAt) {
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
