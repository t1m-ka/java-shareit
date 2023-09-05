package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserValidator.validateUserCreation;
import static ru.practicum.shareit.user.dto.UserValidator.validateUserUpdate;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        if (!validateUserCreation(userDto))
            throw new IllegalArgumentException("The received entity is not correct");
        log.info("Creating user {}", userDto.toString());
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
            @RequestBody @Valid UserDto userDto) {
        if (!validateUserUpdate(userDto))
            throw new IllegalArgumentException("The received entity is not correct");
        log.info("Updating user {}, user={}", userId, userDto.toString());
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Get user {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteUserById(userId);
    }
}
