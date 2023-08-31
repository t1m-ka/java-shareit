package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    long id;

    @NotEmpty(message = "Имя не может быть пустым")
    String name;

    @NotEmpty(message = "Описание не может быть пустым")
    String description;

    Boolean available;

    Long requestId;
}
