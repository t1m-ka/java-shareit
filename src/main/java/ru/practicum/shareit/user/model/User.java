package ru.practicum.shareit.user.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Component
@Data
public class User {
    private long id;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Email не корректен")
    private String email;
}
