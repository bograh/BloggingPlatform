package dtos.response;

public class UserResponseDTO {
    private int userId;
    private String username;
    private String email;
    private String createdAt;

    public UserResponseDTO() {
    }

    public UserResponseDTO(int userId, String username, String email, String createdAt) {
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User {" +
                "\n\tuserId: " + userId +
                ", \n\tusername: '" + username + '\'' +
                ", \n\temail: '" + email + '\'' +
                ", \n\tcreatedAt: '" + createdAt + '\'' +
                '\n' + '}';
    }

}
