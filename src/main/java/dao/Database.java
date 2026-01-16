package dao;

import config.DatabaseConfig;
import exceptions.DatabaseInitializationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Database {

    private static final Logger logger =
            Logger.getLogger(Database.class.getName());

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD
        );
    }

    public static void initDatabase() {
        try {
            createUsersTable();
            createPostsTable();
            createTagsTable();
            createPostTagsTable();
        } catch (SQLException e) {
            throw new DatabaseInitializationException("Database initialization failed.");
        }
    }

    private static void createUsersTable() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id         SERIAL PRIMARY KEY,
                            username   VARCHAR(50)  NOT NULL UNIQUE,
                            email      VARCHAR(100) NOT NULL UNIQUE,
                            password   VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            logger.info("Users table initialized successfully");
        }
    }

    private static void createPostsTable() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS posts (
                            id         SERIAL PRIMARY KEY,
                            title      VARCHAR(255) NOT NULL,
                            body       TEXT         NOT NULL,
                            author_id  INT          NOT NULL,
                            posted_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
                        )
                    """);
            logger.info("Posts table initialized successfully");
        }
    }

    private static void createTagsTable() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS tags (
                            id   SERIAL PRIMARY KEY,
                            name VARCHAR(50) NOT NULL UNIQUE
                        )
                    """);
            logger.info("Tags table initialized successfully");
        }
    }

    private static void createPostTagsTable() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS post_tags (
                            post_id INT NOT NULL,
                            tag_id  INT NOT NULL,
                            PRIMARY KEY (post_id, tag_id),
                            CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
                            CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
                        )
                    """);
            logger.info("PostTags table initialized successfully");
        }
    }
}