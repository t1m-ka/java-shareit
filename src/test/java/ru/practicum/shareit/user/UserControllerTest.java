package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    UserService mockUserService;

    @Test
    void testCreateUser() {

    }
}
