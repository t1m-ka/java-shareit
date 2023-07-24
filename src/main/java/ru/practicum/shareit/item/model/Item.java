package ru.practicum.shareit.item.model;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * TODO Sprint add-controllers.
 */

@Component
@Data
public class Item {
    private long id;

    private String name;

    private String description;

    private boolean status;

    private long owner;
}
