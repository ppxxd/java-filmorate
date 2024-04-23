package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.film.GenreDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceDAL {
    private final GenreDao genreDao;

    public Genre getGenre(Integer id) throws GenreNotFoundException {
        return genreDao.getGenre(id);
    }

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
