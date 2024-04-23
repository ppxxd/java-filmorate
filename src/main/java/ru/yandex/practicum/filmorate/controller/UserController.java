package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceDAL;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserServiceDAL userService;

    @Autowired
    public UserController(UserServiceDAL userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Получен запрос POST /users. Пользователь добавлен.");
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws UserNotFoundException {
        userService.updateUser(user);
        log.info("Получен запрос PUT /users. Пользователь с id {} обновлен.", user.getId());
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос GET /users.");
        return userService.getUsers();
    }

    @PutMapping("/{id}/friends/{friendID}")
    public User addFriend(@PathVariable int id, @PathVariable int friendID) throws UserNotFoundException {
        log.info("Получен запрос PUT /users/{id}/friends/{friendID}. " +
                "Пользователь с id {} добавил в друзья {}.", id, friendID);
        return userService.addFriend(id, friendID);
    }

    @DeleteMapping("/{id}/friends/{friendID}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendID) throws UserNotFoundException {
        log.info("Получен запрос DELETE /users/{id}/friends/{friendID}." +
                " Пользователь с id {} удалил из друзей {}.", id, friendID);
        return userService.deleteFriend(id, friendID);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) throws UserNotFoundException {
        log.info("Получен запрос GET /users/{id}/friends. Получен список друзей пользователя {}.", id);
        System.out.println(userService.getUserFriends(id) + " " + id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getUserMutualFriends(@PathVariable int id, @PathVariable int otherId)
            throws UserNotFoundException {
        log.info("Получен запрос GET /users/{id}/friends/common/{otherId}. " +
                "Получен список общих друзей пользователя {} с пользователем {}.", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}
