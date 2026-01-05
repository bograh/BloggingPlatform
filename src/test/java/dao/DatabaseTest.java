package dao;

import config.H2Database;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    @Test
    void getConnection_shouldReturnValidConnection() throws Exception {
        Connection connection = Database.getConnection();
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

    @Test
    void getConnectionForH2_shouldReturnValidConnection() throws Exception {
        Connection connection = H2Database.getConnection();
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }
}
