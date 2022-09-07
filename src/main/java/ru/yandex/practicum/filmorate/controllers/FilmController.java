package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
    public Film create(@RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос POST /films");
        if(film.getName().isBlank() || film.getDescription().length() > 200 || film.getId() < 0 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895,12,28)) || film.getDuration() <= 0) {
            throw new ValidationException("Введенные данные не соответствуют требуемым критериям");
        }
        film.setId(idFilmCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws IOException {
        log.debug("Получен запрос PUT /films");
        if (!films.containsKey(film.getId())) {
            throw new IOException("Введенные данные не соответствуют требуемым критериям");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }
}
