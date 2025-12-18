package dao;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseTest {

    @Test
    void getConnection_shouldReturnValidConnection() throws Exception {
        Connection conn = Database.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }
}
