package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/myapp?useSSL=false&serverTimezone=UTC";
    private static final String URL = setting("DB_URL", DEFAULT_URL);
    private static final String USERNAME = setting("DB_USER", "root");
    private static final String PASSWORD = setting("DB_PASSWORD", "");

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println(connection.isValid(2)
                    ? "Connected to MySQL successfully."
                    : "MySQL connection is not valid.");
        } catch (SQLException exception) {
            System.err.println("MySQL connection failed: " + exception.getMessage());
        }
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
