package se.jensen.elias.chatapp.dao;

import se.jensen.elias.chatapp.model.Message;

import java.util.List;

public interface MessageDAO {
    void saveMessage(Message message);

    List<Message> getMessagesByUserId(int userId);
}
