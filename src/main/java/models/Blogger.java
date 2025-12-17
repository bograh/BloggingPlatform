package models;

import dtos.UserResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Blogger extends User {
    public Blogger() {
        super();
    }

    public Blogger(int id, String username, String email, String password, LocalDateTime createdAt) {
        super(id, username, email, password, createdAt);
    }

    @Override
    public UserResponseDTO getUserDetails() {
        return new UserResponseDTO(
                this.username,
                this.email,
                this.createdAt.format(DateTimeFormatter.ISO_DATE)
        );
    }

}