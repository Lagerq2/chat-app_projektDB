package se.jensen.elias.chatapp.dao.impl;

import se.jensen.elias.chatapp.dao.MessageDAO;
import se.jensen.elias.chatapp.model.Message;
import se.jensen.elias.chatapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseDAO implements MessageDAO {
    private final Connection conn;
    private final List<Message> messages = new ArrayList<>();

    //Hämtar connection till databasen via singelton klassen
    public MessageDatabaseDAO() {
        try {
            this.conn = DatabaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Kunde inte ansluta till databasen ", e);
        }
    }

    //Sparar meddelandet till databasen
    @Override
    public void saveMessage(Message message) {

        String sql = "INSERT INTO messages (text, user_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, message.getText());
            ps.setInt(2, message.getUserId());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Hämtar användarens meddelanden
    @Override
    public List<Message> getMessagesByUserId(int userId) {
        List<Message> messageList = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                messageList.add(new Message(
                        rs.getInt("user_id"),
                        rs.getString("text"),
                        rs.getTimestamp("timeStamp").toLocalDateTime()
                ));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageList;
    }
}
