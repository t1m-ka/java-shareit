package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "requests")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
}
