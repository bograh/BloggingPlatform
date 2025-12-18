package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider implements ConnectionProvider {

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
    }
}
