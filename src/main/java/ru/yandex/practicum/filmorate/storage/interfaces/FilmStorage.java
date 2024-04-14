package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Set;

public interface FilmStorage {

    Map<Integer, Film> getStorage();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Boolean deleteFilm(Film film); //true if deleted;

    Film getFilmByID(Integer id) throws FilmNotFoundException;

    Set<Integer> getFilmLikes(Integer id);
}
