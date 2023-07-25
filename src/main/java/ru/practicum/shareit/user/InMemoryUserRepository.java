package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<User> addUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return users.containsKey(userId) ? Optional.of(users.get(userId)) : Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteUserById(long userId) {
        return false;
    }
}
