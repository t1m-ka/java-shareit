package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Email не корректен")
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
