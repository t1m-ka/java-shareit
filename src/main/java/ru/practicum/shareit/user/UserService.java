package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(User user, long userId);

    User getUserById(long userId);

    List<User> getAllUsers();

    void deleteUserById(long userId);
}
