package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmLikesDao;
import ru.yandex.practicum.filmorate.storage.dao.film.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.film.MpaDao;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmServiceDAL {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);
    private final FilmDao filmDao;
    private final UserServiceDAL userService;
    private final GenreDao genreDao;
    private final FilmLikesDao filmLikesDao;
    private final MpaDao mpaDao;

    @Autowired
    public FilmServiceDAL(@Qualifier("FilmDaoImpl") FilmDao filmDao, UserServiceDAL userService,
                         GenreDao genreDao, FilmLikesDao filmLikesDao, MpaDao mpaDao) {
        this.filmDao = filmDao;
        this.userService = userService;
        this.genreDao = genreDao;
        this.filmLikesDao = filmLikesDao;
        this.mpaDao = mpaDao;
    }

    public Film createFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("Неверная дата релиза");
        }
        try {
            mpaDao.getMpa(film.getMpa().getId());
        } catch (MpaNotFoundException e) {
            throw new ValidationException(e.getMessage());
        }
        if (film.getGenres() != null) {
            try {
                genreDao.getGenre(film.getGenres().get(0).getId());
            } catch (GenreNotFoundException e) {
                throw new ValidationException(e.getMessage());
            }
        }


        Film newFilm = filmDao.addFilm(film);
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            newFilm.setGenres(new ArrayList<>());
        } else {
            for (Integer genreId : film.getGenres().stream().map(Genre::getId).sorted().collect(Collectors.toCollection(LinkedHashSet::new))) {
                genreDao.addFilmGenre(newFilm.getId(), genreId);
            }
            newFilm.setGenres(genreDao.getFilmGenre(film.getId()));
        }
        return newFilm;
    }

    public Film updateFilm(Film film) throws ValidationException {
        if (!filmDao.checkFilmExist(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким id уже существует.");
        }
        try {
            mpaDao.getMpa(film.getMpa().getId());
        } catch (MpaNotFoundException e) {
            throw new ValidationException(e.getMessage());
        }

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            genreDao.deleteFilmGenre(film.getId());
            film.setGenres(new ArrayList<>());
        } else {
            genreDao.deleteFilmGenre(film.getId());
            for (Genre genre : film.getGenres()) {
                genreDao.addFilmGenre(film.getId(), genre.getId());
            }
            film.setGenres(genreDao.getFilmGenre(film.getId()));
        }
        return filmDao.updateFilm(film);
    }

    public Film likeFilm(Integer filmId, Integer userId) {
        filmDao.checkFilmExist(filmId);
        userService.getUser(userId); // метод getUser() выбросит исключение, если userId не существует
        filmLikesDao.like(filmId, userId);
        return filmDao.getFilmByID(filmId);
    }

    public Film unlikeFilm(Integer filmId, Integer userId) {
        filmDao.checkFilmExist(filmId);
        userService.getUser(userId); // метод getUser() выбросит исключение, если userId не существует
        filmLikesDao.unlike(filmId, userId);
        return filmDao.getFilmByID(filmId);
    }

    public Film getFilmByID(Integer id) {
        filmDao.checkFilmExist(id);
        Film film = filmDao.getFilmByID(id);
        film.setGenres(genreDao.getFilmGenre(film.getId()));
        return film;
    }

    public List<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        for (Film film : filmDao.getFilms()) {
            film.setGenres(genreDao.getFilmGenre(film.getId()));
            films.add(film);
        }
        return films;
    }

    public List<Film> getMostPopular(Integer count) {
        return filmDao.getMostPopularFilms(count);
    }
}
