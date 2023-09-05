package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.UserAlreadyExistsException;
import ru.practicum.shareit.util.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return toUserDto(repository.save(toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User currentUser = repository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        repository.findByEmailIs(userDto.getEmail()).ifPresent(x -> {
            if (x.getId() != userId)
                throw new UserAlreadyExistsException("Пользователь с таким адресом уже зарегистрирован");
        });
        if (userDto.getName() != null)
            currentUser.setName(userDto.getName());
        if (userDto.getEmail() != null)
            currentUser.setEmail(userDto.getEmail());
        return toUserDto(repository.save(currentUser));
    }

    @Override
    public UserDto getUserById(long userId) {
        return toUserDto(
                repository.findById(userId).orElseThrow(
                        () -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден")));
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
                () -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));
        repository.deleteById(userId);
    }
}
