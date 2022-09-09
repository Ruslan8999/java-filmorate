package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {
    private int idFilmCounter = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос POST /films");
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            throw new ValidationException("Указываемая дата релиза не должна быть ранее 28.12.1895 года");
        }
        film.setId(idFilmCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) throws IOException {
        log.debug("Получен запрос PUT /films");
        if (!films.containsKey(film.getId())) {
            throw new IOException("Введен некорректный номер id = " + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }
}
