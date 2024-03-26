package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    Map<Integer, User> users = UserStorage.getStorage();

    @PostMapping("/users")
    public User add(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id уже существует.");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(UserStorage.generateID());
        users.put(user.getId(), user);
        log.info("Получен запрос POST /users. Пользователь с id {} добавлен.", user.getId());
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id не существует.");
        }
        users.put(user.getId(), user);
        log.info("Получен запрос PUT /users. Пользователь с id {} обновлен.", user.getId());
        return user;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Получен запрос GET /users.");
        return new ArrayList<>(users.values());
    }
}
