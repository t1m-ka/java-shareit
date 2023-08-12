package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

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
    public Optional<User> updateUser(User user, long userId) {
        User updatedUser = users.get(userId);
        if (user.getName() != null && !user.getName().isBlank())
            updatedUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().isBlank())
            updatedUser.setEmail(user.getEmail());
        return Optional.of(updatedUser);
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
        if (users.containsKey(userId)) {
            users.remove(userId);
            return true;
        }
        return false;
    }
}
