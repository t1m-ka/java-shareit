package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(User user) {
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(User user, long userId) {
        User currentUser = repository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));

        repository.findByEmailIs(user.getEmail()).ifPresent(x -> {
            if (x.getId() != userId)
                throw new UserAlreadyExistsException("Пользователь с таким адресом уже зарегистрирован");
        });
        if (user.getName() != null)
            currentUser.setName(user.getName());
        if (user.getEmail() != null)
            currentUser.setEmail(user.getEmail());
        return UserMapper.toUserDto(repository.save(currentUser));
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(
                repository.findById(userId).orElseThrow(
                        () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long userId) {
        repository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));
        repository.deleteById(userId);
    }
}
