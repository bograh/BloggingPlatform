package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider implements ConnectionProvider {

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                H2DatabaseConfig.DB_URL,
                H2DatabaseConfig.DB_USER,
                H2DatabaseConfig.DB_PASSWORD
        );
    }
}
