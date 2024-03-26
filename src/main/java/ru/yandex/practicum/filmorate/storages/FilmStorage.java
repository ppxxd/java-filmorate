package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

public class FilmStorage {
    private static int id = 1;
    private static final Map<Integer, Film> films = new HashMap<>();

    public static int generateID() {
        return id++;
    }

    public static Map<Integer, Film> getStorage() {
        return films;
    }

}
