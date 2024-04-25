package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film) throws ValidationException;

    Film updateFilm(Film film);

    Boolean deleteFilm(Film film); //true if deleted;

    Film getFilmByID(Integer id) throws FilmNotFoundException;

    Set<Integer> getFilmLikes(Integer id);

    List<Film> getFilms();

    List<Film> getMostPopularFilms(Integer count);
}
