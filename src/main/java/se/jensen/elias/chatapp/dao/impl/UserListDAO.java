package se.jensen.elias.chatapp.dao.impl;


import se.jensen.elias.chatapp.dao.UserDAO;
import se.jensen.elias.chatapp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserListDAO implements UserDAO {

    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @Override
    public User register(User user) {
        user.setId(nextId++);
        users.add(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        Optional<User> found = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
        return found.orElse(null);
    }

    // valfritt: för testsyfte
    public void seedUsers() {
        register(new User("test", "123"));
        register(new User("hakan", "password"));
    }
}

