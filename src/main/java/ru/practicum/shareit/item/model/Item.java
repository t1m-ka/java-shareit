package ru.practicum.shareit.item.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@Data
public class Item {
    private long id;

    @NotEmpty(message = "Наименование не может быть пустым")
    private String name;

    @NotEmpty(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус не может быть пустым")
    private Boolean available;

    private long owner;
}
