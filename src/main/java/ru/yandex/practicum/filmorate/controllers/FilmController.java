package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Получен запрос GET /films");
        return service.findAll();
    }

    @GetMapping("{id}")
    Optional<Film> findById(@PathVariable int id){
        return service.findById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(required = false, defaultValue = "10", name = "count") Integer count,
                                       @RequestParam(required = false, name = "genreId") @Nullable Integer genreId,
                                       @RequestParam(required = false, name = "year") @Nullable Integer date) {
        return service.getMostPopular(count, genreId, date);
    }

    @GetMapping("common")
    public Collection<Film> getCommonFilms(
            @RequestParam int userId,
            @RequestParam int friendId) {
        log.info("getCommonFilms");
        return service.getCommonFilms(userId, friendId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен запрос POST /films");
        if (film.getMpa() == null) {
            throw new InternalServerException();
        }
        service.createFilm(film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Получен запрос PUT /films");
        return service.updateFilm(film);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) throws IOException {
        service.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        service.removeLike(id, userId);
    }

    @DeleteMapping("{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        service.removeFilm(filmId);
    }
}
