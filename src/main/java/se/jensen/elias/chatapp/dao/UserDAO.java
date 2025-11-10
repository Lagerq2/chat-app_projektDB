package se.jensen.elias.chatapp.dao;

import se.jensen.elias.chatapp.model.User;

public interface UserDAO {
    User login(String username, String password);

    User register(User user);
}
