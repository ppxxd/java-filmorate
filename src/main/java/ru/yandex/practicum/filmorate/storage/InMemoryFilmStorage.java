package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public Map<Integer, Film> getStorage() {
        return new HashMap<>(films);
    }

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Boolean deleteFilm(Film film) {
        films.remove(film.getId());
        return true;
    }

    @Override
    public Film getFilmByID(Integer id) throws FilmNotFoundException {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }
        return films.get(id);
    }

    @Override
    public Set<Integer> getFilmLikes(Integer id) {
        return films.get(id).getLikes();
    }
}
