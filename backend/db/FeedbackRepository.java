package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class FeedbackRepository {
    private FeedbackRepository() {
    }

    public static Feedback findLatestForUser(String username) throws SQLException {
        String sql = "SELECT ID, USERNAME, FEEDBACK, CREATED_AT FROM feedback "
                + "WHERE USERNAME = ? ORDER BY UPDATED_AT DESC LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? readFeedback(results) : null;
            }
        }
    }

    public static Feedback add(String username, String text) throws SQLException {
        String sql = "INSERT INTO feedback (USERNAME, FEEDBACK) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, text);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Feedback(keys.getInt(1), username, text, "");
                }
            }
            throw new SQLException("Could not create feedback");
        }
    }

    public static void update(int id, String text) throws SQLException {
        String sql = "UPDATE feedback SET FEEDBACK = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, text);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM feedback WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public static List<Feedback> findAll() throws SQLException {
        String sql = "SELECT ID, USERNAME, FEEDBACK, CREATED_AT FROM feedback ORDER BY ID";
        List<Feedback> feedback = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                feedback.add(readFeedback(results));
            }
        }
        return feedback;
    }

    private static Feedback readFeedback(ResultSet results) throws SQLException {
        return new Feedback(
                results.getInt("ID"),
                results.getString("USERNAME"),
                results.getString("FEEDBACK"),
                results.getString("CREATED_AT"));
    }

    public static final class Feedback {
        private final int id;
        private final String username;
        private final String text;
        private final String date;

        public Feedback(int id, String username, String text, String date) {
            this.id = id;
            this.username = username;
            this.text = text;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getText() {
            return text;
        }

        public String getDate() {
            return date;
        }
    }
}