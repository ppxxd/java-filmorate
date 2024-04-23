package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.film.MpaDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceDAL {
    private final MpaDao mpaDao;

    public MPA getMpa(Integer id) throws MpaNotFoundException {
        return mpaDao.getMpa(id);
    }

    public List<MPA> getAllMpa() {
        return mpaDao.getAllMpa();
    }
}