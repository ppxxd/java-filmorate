package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MpaDao {
    MPA getMpa(Integer id);

    List<MPA> getAllMpa();
}
