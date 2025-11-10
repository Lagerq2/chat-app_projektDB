package se.jensen.elias.chatapp.dao.impl;


import se.jensen.elias.chatapp.dao.MessageDAO;
import se.jensen.elias.chatapp.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageListDAO implements MessageDAO {

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void saveMessage(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) {
        return messages.stream()
                .filter(m -> m.getUserId() == userId)
                .collect(Collectors.toList());
    }

    // valfritt: för att testa lättare
    public void seedTestData() {
        messages.add(new Message(1, "Hej!", LocalDateTime.now()));
        messages.add(new Message(2, "Hallå där!", LocalDateTime.now()));
    }
}

