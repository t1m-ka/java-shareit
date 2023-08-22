package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exception.UserAlreadyExistsException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class UserServiceImplTest {

    private final EntityManager em;

    private final UserServiceImpl service;


    @Test
    @Transactional
    void saveUserSuccess() {
        User newUser = new User("user1", "user1@mail.ru");

        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));
    }

    @Test
    @Transactional
    void saveUserWithEmptyNameShouldThrowException() {
        User newUser = new User("", "user1@mail.ru"); // Пустое поле name

        assertThrows(ConstraintViolationException.class, () -> {
            service.addUser(newUser);
        });
    }

    @Test
    @Transactional
    void saveUserWithEmptyEmailShouldThrowException() {
        User newUser = new User("failUser", ""); // Пустое поле email

        assertThrows(ConstraintViolationException.class, () -> {
            service.addUser(newUser);
        });
    }

    @Test
    @Transactional
    void saveUserWithNotUniqueEmailShouldThrowException() {
        User newUser = new User("failUser", "user1@mail.ru");
        service.addUser(newUser);

        User repeatUser = new User("failUser", "user1@mail.ru");

        assertThrows(DataIntegrityViolationException.class, () -> {
            service.addUser(repeatUser);
        });
    }

    @Test
    @Transactional
    void updateUserSuccess() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        User updatedUser = new User(user.getId(), "user1Upd", "user1Upd@mail.ru");
        service.updateUser(updatedUser, user.getId());

        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        user = query.setParameter("email", updatedUser.getEmail()).getSingleResult();

        assertThat(user.getId(), equalTo(updatedUser.getId()));
        assertThat(user.getName(), equalTo(updatedUser.getName()));
        assertThat(user.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    @Transactional
    void updateUserWithoutNameSuccess() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        User updatedUser = User.builder().id(user.getId()).email("user1Upd@mail.ru").build();
        service.updateUser(updatedUser, user.getId());

        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        user = query.setParameter("email", updatedUser.getEmail()).getSingleResult();

        assertThat(user.getId(), equalTo(updatedUser.getId()));
        assertThat(user.getName(), equalTo("user1"));
        assertThat(user.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    @Transactional
    void updateUserWrongIdShouldThrowException() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        User updatedUser = new User(1, "user1Upd", "user1Upd@mail.ru");

        assertThrows(UserNotFoundException.class, () -> {
            service.updateUser(updatedUser, 1000);
        });
    }

    @Test
    @Transactional
    void updateUserWithNotUniqueEmailShouldThrowException() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        newUser = new User("user2", "user2@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        User updatedUser = new User(user.getId(), "user1Upd", "user1@mail.ru");

        assertThrows(UserAlreadyExistsException.class, () -> {
            service.updateUser(updatedUser, user.getId());
        });
    }

    @Test
    @Transactional
    void getUserByIdSuccess() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        UserDto receivedUser = service.getUserById(user.getId());

        assertThat(user.getId(), equalTo(receivedUser.getId()));
        assertThat(user.getName(), equalTo(receivedUser.getName()));
        assertThat(user.getEmail(), equalTo(receivedUser.getEmail()));
    }

    @Test
    @Transactional
    void getUserByWrongIdShouldThrowException() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        assertThrows(UserNotFoundException.class, () -> {
            service.getUserById(1000);
        });
    }

    @Test
    @Transactional
    void getAllUsersSuccess() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        newUser = new User("user2", "user2@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<UserDto> userList = query.getResultList().stream().map(UserMapper::toUserDto).collect(Collectors.toList());

        List<UserDto> receivedUserList = service.getAllUsers();

        assertThat(receivedUserList, equalTo(userList));
    }

    @Test
    @Transactional
    void deleteUserByIdSuccess() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        service.deleteUserById(user.getId());

        assertThrows(NoResultException.class, () -> {
            em.createQuery("Select u from User u where u.id = :id", User.class)
                    .setParameter("id", user.getId())
                    .getSingleResult();
        });
    }

    @Test
    @Transactional
    void deleteUserByWrongIdShouldThrowException() {
        User newUser = new User("user1", "user1@mail.ru");
        service.addUser(newUser);

        assertThrows(UserNotFoundException.class, () -> {
            service.deleteUserById(1000);
        });
    }
}
