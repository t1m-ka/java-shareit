package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return service.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody User user, @PathVariable long userId) {
        return service.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return service.getUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        service.deleteUserById(userId);
    }
}
