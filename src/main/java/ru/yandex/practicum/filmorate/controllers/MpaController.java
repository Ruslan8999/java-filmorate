package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping()
    public List<Mpa> getAll() {
        log.debug("Mpa getAll");
        return mpaService.getAll();
    }

    @GetMapping("{id}")
    public Optional<Mpa> getMpaById(@PathVariable Integer id) {
        if (id < 1) {
            throw new UnableToFindException();
        }
        log.debug("getMpaById: " + id);
        return mpaService.getById(id);
    }
}
