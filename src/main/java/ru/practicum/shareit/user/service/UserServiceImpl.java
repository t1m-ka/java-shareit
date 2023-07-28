package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(User user) {
        if (checkEmailExist(user).isPresent())
            throw new UserAlreadyExistsException("Пользователь с таким адресом уже зарегистрирован");
        Optional<User> newUser = repository.addUser(user);
        if (newUser.isPresent())
            return UserMapper.toUserDto(newUser.get());
        else
            throw new NullPointerException();
    }

    @Override
    public UserDto updateUser(User user, long userId) {
        Optional<User> checkedUser = checkEmailExist(user);
        if (checkedUser.isPresent()) {
            if (checkedUser.get().getId() != userId) {
                throw new UserAlreadyExistsException("Пользователь с таким адресом уже зарегистрирован");
            }
        }
        Optional<User> newUser = repository.updateUser(user, userId);
        if (newUser.isPresent())
            return UserMapper.toUserDto(newUser.get());
        else
            throw new NullPointerException();
    }

    @Override
    public UserDto getUserById(long userId) {
        Optional<User> user = repository.getUserById(userId);
        if (user.isPresent())
            return UserMapper.toUserDto(user.get());
        else
            throw new NullPointerException();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long userId) {
        if (!repository.deleteUserById(userId))
            throw new NullPointerException();
    }

    public Optional<User> checkEmailExist(User user) {
        return repository.getAllUsers().stream()
                .filter(x -> x.getEmail().equals(user.getEmail()))
                .findFirst();
    }
}
