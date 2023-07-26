package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> addUser(User user);

    Optional<User> updateUser(User user, long userId);

    Optional<User> getUserById(long userId);

    List<User> getAllUsers();

    boolean deleteUserById(long userId);
}
