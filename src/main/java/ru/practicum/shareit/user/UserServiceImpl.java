package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User addUser(User user) {
        Optional<User> newUser = repository.addUser(user);
        if (newUser.isPresent())
            return newUser.get();
        else
            throw new NullPointerException();
    }

    @Override
    public User updateUser(User user, long userId) {
        Optional<User> newUser = repository.updateUser(user, userId);
        if (newUser.isPresent())
            return newUser.get();
        else
            throw new NullPointerException();
    }

    @Override
    public User getUserById(long userId) {
        Optional<User> user = repository.getUserById(userId);
        if (user.isPresent())
            return user.get();
        else
            throw new NullPointerException();
    }

    @Override
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public void deleteUserById(long userId) {
        if (!repository.deleteUserById(userId))
            throw new NullPointerException();
    }
}
