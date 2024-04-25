package ru.yandex.practicum.filmorate.service.inmemory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;
    private static int id = 1;

    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("inMemoryFilmStorage") FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public int generateID() {
        return id++;
    }

    public Film likeFilm(Integer filmID, Integer userID) throws FilmNotFoundException, UserNotFoundException {
        Film film = storage.getFilmByID(filmID);
        User user = userService.getStorage().getUserByID(userID);
        film.getLikes().add(userID);
        return film;
    }

    public Film unlikeFilm(Integer filmID, Integer userID) throws FilmNotFoundException, UserNotFoundException {
        Film film = storage.getFilmByID(filmID);
        User user = userService.getStorage().getUserByID(userID);
        film.getLikes().remove(userID);
        return film;
    }

    public List<Film> getMostPopular(int count) {
        return storage.getMostPopularFilms(count);
    }

    public  Film createFilm(Film film) throws ValidationException {
        if (storage.getFilmByID(film.getId()) == null) {
            throw new ValidationException("Фильм с таким id уже существует.");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("Неверная дата релиза");
        }
        film.setId(generateID());
        return storage.addFilm(film);
    }

    public  Film updateFilm(Film film) throws FilmNotFoundException {
        if (storage.getFilmByID(film.getId()) == null) {
            throw new FilmNotFoundException("Фильма с таким id не существует.");
        }
        return storage.updateFilm(film);
    }

    public List<Film> findAll() {
        return new ArrayList<>(storage.getFilms());
    }
}
