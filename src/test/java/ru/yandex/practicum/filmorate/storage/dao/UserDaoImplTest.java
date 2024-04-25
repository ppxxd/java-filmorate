package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;
import ru.yandex.practicum.filmorate.storage.dao.user.impl.FriendshipDaoImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDaoImplTest {
    private final UserDao userStorage;
    private final FriendshipDaoImpl friendStorage;
    private User user;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("user1@gmail.com")
                .login("user1")
                .name("user1 name")
                .birthday(LocalDate.of(1980, 5, 25))
                .build();
    }

    @Test
    void shouldCreateUser() {
        User newUser = userStorage.addUser(user);
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldUpdateUser() {
        userStorage.addUser(user);
        userStorage.updateUser(
                User.builder()
                        .id(1)
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());
        assertEquals("andrey", userStorage.getUserByID(1).getLogin());
        assertEquals("Andrey", userStorage.getUserByID(1).getName());
        Set<ConstraintViolation<User>> violations = validator.validate(userStorage.getUserByID(1));
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldGetListUsersWithoutViolations() {
        userStorage.addUser(user);
        userStorage.addUser(User.builder()
                .email("user2@gmail.com")
                .login("user2")
                .name("userName")
                .birthday(LocalDate.of(1995, 7, 25))
                .build()
        );
        userStorage.addUser(User.builder()
                .email("user3@gmail.com")
                .login("user3")
                .name("userName")
                .birthday(LocalDate.of(1989, 4, 15))
                .build()
        );
        Optional<Set<ConstraintViolation<User>>> violationSet = userStorage.getUsers().stream()
                .map(user -> validator.validate(user))
                .filter(violation -> !violation.isEmpty())
                .findFirst();
        violationSet.ifPresentOrElse(
                violation -> assertTrue(violation.isEmpty()),
                () -> assertTrue(true)
        );
    }

    @Test
    void shouldGetUserById() {
        userStorage.addUser(user);
        assertEquals(user, userStorage.getUserByID(1));
    }

    @Test
    void shouldThrowsInvalidCheckUserExist() {
        userStorage.addUser(user);

        final UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> userStorage.checkUserExist(2)
        );
        assertEquals("Пользователь с id 2 не найден.", e.getMessage());
    }

    @Test
    void shouldAddFriend() {
        userStorage.addUser(user);
        User newUser = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());
        friendStorage.addFriend(user.getId(), newUser.getId());

        assertFalse(userStorage.getUserFriends(user.getId()).isEmpty());
        assertTrue(userStorage.getUserFriends(newUser.getId()).isEmpty());
    }

    @Test
    void shouldDeleteFriend() {
        userStorage.addUser(user);
        User newUser = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());

        friendStorage.addFriend(user.getId(), newUser.getId());
        assertFalse(userStorage.getUserFriends(user.getId()).isEmpty());

        friendStorage.deleteFriend(user.getId(), newUser.getId());
        assertTrue(userStorage.getUserFriends(user.getId()).isEmpty());
    }

    @Test
    void shouldConfirmFriendship() {
        userStorage.addUser(user);
        User newUser = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());

        friendStorage.addFriend(user.getId(), newUser.getId());
        friendStorage.addFriend(newUser.getId(), user.getId());

        assertFalse(userStorage.getUserFriends(user.getId()).isEmpty());
        assertFalse(userStorage.getUserFriends(newUser.getId()).isEmpty());
    }

    @Test
    void shouldGetCommonFriends() {
        userStorage.addUser(user);
        User newUser1 = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());

        User newUser2 = userStorage.addUser(
                User.builder()
                        .email("ivan@gmail.com")
                        .login("vano")
                        .name("Ivan")
                        .birthday(LocalDate.of(2003, 12, 12))
                        .build()
        );
        friendStorage.addFriend(user.getId(), newUser1.getId());
        friendStorage.addFriend(newUser2.getId(), newUser1.getId());

        assertEquals(Set.of(newUser1), friendStorage.getCommonFriends(user.getId(), newUser2.getId()));
    }

    @Test
    void shouldGetAllFriends() {
        userStorage.addUser(user);
        User newUser1 = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());

        User newUser2 = userStorage.addUser(
                User.builder()
                        .email("ivan@gmail.com")
                        .login("vano")
                        .name("Ivan")
                        .birthday(LocalDate.of(2003, 12, 12))
                        .build()
        );
        friendStorage.addFriend(user.getId(), newUser1.getId());
        friendStorage.addFriend(user.getId(), newUser2.getId());

        assertEquals(List.of(newUser1, newUser2), userStorage.getUserFriends(user.getId()));
    }
}