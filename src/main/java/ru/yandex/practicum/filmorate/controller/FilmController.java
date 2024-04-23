package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceDAL;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmServiceDAL filmService;

    @Autowired
    public FilmController(FilmServiceDAL filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Получен запрос POST /films. Фильм добавлен.");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws FilmNotFoundException, ValidationException {
        log.info("Получен запрос PUT /films. Фильм с id {} обновлен.", film.getId());
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос GET /films.");
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) throws FilmNotFoundException,
            UserNotFoundException {
        log.info("Получен запрос PUT /films/{id}/like/{userId}. " +
                "Пользователь {} поставил лайк к фильму {}.", id, userId);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film unlikeFilm(@PathVariable int id, @PathVariable int userId)
            throws FilmNotFoundException, UserNotFoundException {
        log.info("Получен запрос PUT /films/{id}/like/{userId}. " +
                "Пользователь {} убрал лайк к фильму {}.", id, userId);
        return filmService.unlikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос PUT /films/{id}/like/{userId}. Запрошен список {} популярных фильмов.", count);
        return filmService.getMostPopular(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer filmId) {
        log.info("Получен запрос GET /films/{id}. Запрошен фильм с id {}.", filmId);
        return filmService.getFilmByID(filmId);
    }
}
