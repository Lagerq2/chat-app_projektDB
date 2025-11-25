package se.jensen.elias.chatapp.dao.impl;


import se.jensen.elias.chatapp.dao.UserDAO;
import se.jensen.elias.chatapp.model.Message;
import se.jensen.elias.chatapp.model.User;
import se.jensen.elias.chatapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseDAO implements UserDAO {

    private final Connection conn;

    // Konstruktor som jag kan använda i mitt test
    public UserDatabaseDAO(Connection conn) {
        this.conn = conn;
    }

    public UserDatabaseDAO() {
        //hämtar en connection instans till databasen
        try {
            this.conn = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Kunde inte ansluta till databasen ", e);
        }

    }

    //Login metod med en Join där användarens meddelanden hämtas
    @Override
    public User login(String username, String password) {
        //sql left join som hämtar användaruppgifter plus meddelanden
        String sql = """
                SELECT
                    u.id AS user_id,
                    u.username,
                    u.password,
                    m.text,
                    m.timestamp
                FROM users u
                LEFT JOIN messages m ON u.id = m.user_id
                WHERE u.username = ?
                  AND u.password = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            User user = null;
            List<Message> savedMessages = new ArrayList<>();

            while (rs.next()) {
                if (user == null) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
                String text = rs.getString("text");
                Timestamp ts = rs.getTimestamp("timestamp");
                if (text != null) {
                    savedMessages.add(new Message(
                            rs.getInt("user_id"),
                            text,
                            ts.toLocalDateTime()
                    ));
                }
            }
            if (user != null) {
                user.setMessages(savedMessages);
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Problem med inloggning: " + e.getMessage());
        }
    }

    @Override
    public User register(User user) {

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
