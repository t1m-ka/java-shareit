package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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

@Transactional
@SpringBootTest(
        properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;

    private final UserServiceImpl service;

    private User newUser;

    @BeforeEach
    void setUp() {
        newUser = new User("user1", "user1@mail.ru");
    }

    @Test
    void testCorrectSaveUser() {
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));
    }

    @Test
    void testSaveUserWithEmptyNameShouldThrowException() {
        newUser.setName("");

        assertThrows(ConstraintViolationException.class, () -> {
            service.addUser(newUser);
        });
    }

    @Test
    void testSaveUserWithEmptyEmailShouldThrowException() {
        newUser.setEmail("");

        assertThrows(ConstraintViolationException.class, () -> {
            service.addUser(newUser);
        });
    }

    @Test
    void testSaveUserWithNotUniqueEmailShouldThrowException() {
        service.addUser(newUser);

        User repeatUser = new User("failUser", "user1@mail.ru");

        assertThrows(DataIntegrityViolationException.class, () -> {
            service.addUser(repeatUser);
        });
    }

    @Test
    void testCorrectUpdateUser() {
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
    void testCorrectUpdateUserWithoutName() {
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
    void testUserWrongIdShouldThrowException() {
        service.addUser(newUser);

        User updatedUser = new User(1, "user1Upd", "user1Upd@mail.ru");

        assertThrows(UserNotFoundException.class, () -> {
            service.updateUser(updatedUser, 1000);
        });
    }

    @Test
    void testUpdateUserWithNotUniqueEmailShouldThrowException() {
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
    void testCorrectGetUserById() {
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail()).getSingleResult();

        UserDto receivedUser = service.getUserById(user.getId());

        assertThat(user.getId(), equalTo(receivedUser.getId()));
        assertThat(user.getName(), equalTo(receivedUser.getName()));
        assertThat(user.getEmail(), equalTo(receivedUser.getEmail()));
    }

    @Test
    void testGetUserByWrongIdShouldThrowException() {
        service.addUser(newUser);

        assertThrows(UserNotFoundException.class, () -> {
            service.getUserById(1000);
        });
    }

    @Test
    void testCorrectGetAllUsers() {
        service.addUser(newUser);

        newUser = new User("user2", "user2@mail.ru");
        service.addUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u order by id asc", User.class);
        List<UserDto> userList = query.getResultList().stream().map(UserMapper::toUserDto).collect(Collectors.toList());

        List<UserDto> receivedUserList = service.getAllUsers();

        assertThat(receivedUserList.get(0), equalTo(userList.get(0)));
        assertThat(receivedUserList.get(1), equalTo(userList.get(1)));
    }

    @Test
    void testCorrectDeleteUserById() {
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
    void testDeleteUserByWrongIdShouldThrowException() {
        service.addUser(newUser);

        assertThrows(UserNotFoundException.class, () -> {
            service.deleteUserById(1000);
        });
    }
}
