package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    long id;

    @NotEmpty(message = "Имя не может быть пустым")
    String name;

    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Email не корректен")
    String email;
}
