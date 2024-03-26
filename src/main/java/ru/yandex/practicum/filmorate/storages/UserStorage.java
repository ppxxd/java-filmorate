package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private static int id = 1;
    private static final Map<Integer, User> users = new HashMap<>();

    public static int generateID() {
        return id++;
    }

    public static Map<Integer, User> getStorage() {
        return users;
    }
}
