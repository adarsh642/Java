package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserRepository {
    private UserRepository() {
    }

    public static void register(String username, String email, String password) throws SQLException {
        String sql = "INSERT INTO `user` (USERNAME, EMAIL, PASSWORD) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, PasswordUtil.hashPassword(password));
            statement.executeUpdate();
        }
    }

    public static Account authenticate(String username, String password) throws SQLException {
        String sql = "SELECT USERNAME, USER_ROLE, PASSWORD FROM `user` WHERE USERNAME = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next() && PasswordUtil.verifyPassword(password, results.getString("PASSWORD"))) {
                    return new Account(results.getString("USERNAME"), results.getString("USER_ROLE"));
                }
                return null;
            }
        }
    }

    public static final class Account {
        private final String username;
        private final String role;

        public Account(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public boolean isAdmin() {
            return "ADMIN".equalsIgnoreCase(role);
        }
    }
}