package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaServiceDAL;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaServiceDAL mpaService;

    @GetMapping("/{id}")
    public MPA getMpa(@PathVariable int id) {
        return mpaService.getMpa(id);
    }

    @GetMapping
    public List<MPA> getAllMpa() {
        return mpaService.getAllMpa();
    }
}
