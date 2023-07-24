package ru.practicum.shareit.user;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * TODO Sprint add-controllers.
 */

@Component
@Data
public class User {
    private long id;

    private String name;

    private String email;
}
