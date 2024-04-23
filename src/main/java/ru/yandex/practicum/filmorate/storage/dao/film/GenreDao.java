package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    void addFilmGenre(Integer filmId, Integer genreId) throws ValidationException;

    void deleteFilmGenre(Integer filmId);

    List<Genre> getFilmGenre(Integer filmId);

    Genre getGenre(Integer id);

    List<Genre> getAllGenres();
}
