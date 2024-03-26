package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    Map<Integer, Film> films = FilmStorage.getStorage();
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id уже существует.");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("Неверная дата релиза");
        }
        film.setId(FilmStorage.generateID());
        films.put(film.getId(), film);
        log.info("Получен запрос POST /films. Фильм с id {} добавлен.", film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с таким id не существует.");
        }
        films.put(film.getId(), film);
        log.info("Получен запрос PUT /films. Фильм с id {} обновлен.", film.getId());
        return film;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Получен запрос GET /films.");
        return new ArrayList<>(films.values());
    }
}
