package utils;

import dtos.request.CreateUserDTO;

import java.util.Random;

public class RandomUserGenerator {

    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public static String randomUsername() {
        return randomString(8);
    }

    public static String randomEmail() {
        return randomString(6) + "@" + randomString(4) + ".com";
    }

    public static CreateUserDTO randomUser() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername(randomUsername());
        dto.setEmail(randomEmail());
        dto.setPassword("password");
        return dto;
    }
}
