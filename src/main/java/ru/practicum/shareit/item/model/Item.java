package ru.practicum.shareit.item.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-controllers.
 */

@Component
@Data
public class Item {
    private long id;

    @NotEmpty(message = "Наименование не может быть пустым")
    private String name;

    private String description;

    private Boolean available;

    private long owner;
}
