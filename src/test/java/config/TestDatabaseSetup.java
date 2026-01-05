package config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabaseSetup {
    public static void reset(ConnectionProvider provider) throws SQLException {
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {

            dropTables(stmt);
            createSchema(stmt);
        }

    }

    private static void createSchema(Statement stmt) throws SQLException {
        stmt.execute("""
                    CREATE TABLE users (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        username VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

        stmt.execute("""
                    CREATE TABLE tags (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL UNIQUE
                    )
                """);

        stmt.execute("""
                    CREATE TABLE posts (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(255) NOT NULL,
                        body TEXT NOT NULL,
                        author_id INT NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (author_id) REFERENCES users(id)
                    )
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS comments (
                        id SERIAL PRIMARY KEY,
                        content TEXT NOT NULL,
                        author_id INT NOT NULL,
                        post_id INT NOT NULL,
                        commented_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
                        CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
                    )
                """);

        stmt.execute("""
                    CREATE TABLE post_tags (
                        post_id INT NOT NULL,
                        tag_id INT NOT NULL,
                        PRIMARY KEY (post_id, tag_id),
                        CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                        CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
                    )
                """);
    }

    private static void dropTables(Statement stmt) throws SQLException {
        stmt.execute("DROP TABLE IF EXISTS post_tags");
        stmt.execute("DROP TABLE IF EXISTS tags");
        stmt.execute("DROP TABLE IF EXISTS comments");
        stmt.execute("DROP TABLE IF EXISTS posts");
        stmt.execute("DROP TABLE IF EXISTS users");
    }
}
