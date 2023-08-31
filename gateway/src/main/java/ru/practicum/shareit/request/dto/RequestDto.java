package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private long id;

    @NotEmpty(message = "Описание не может быть пустым")
    private String description;

    private String created;
}
