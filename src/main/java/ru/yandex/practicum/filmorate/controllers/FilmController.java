package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
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
        log.debug("findAll");
        return service.findAll();
    }

    @GetMapping("{id}")
    Optional<Film> findById(@PathVariable int id){
        log.debug("findById: " + id);
        return service.findById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(required = false, defaultValue = "10", name = "count") Integer count,
                                       @RequestParam(required = false, name = "genreId") @Nullable Integer genreId,
                                       @RequestParam(required = false, name = "year") @Nullable Integer date) {
        log.debug("getPopular");
        return service.getMostPopular(count, genreId, date);
    }

    @GetMapping("common")
    public Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.debug("getCommonFilms");
        return service.getCommonFilms(userId, friendId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getMpa() == null) {
            throw new InternalServerException();
        }
        log.debug("createFilm: " + film);
        service.createFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("updateFilm: " + film);
        return service.updateFilm(film);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        service.removeLike(id, userId);
    }

    @DeleteMapping("{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        log.debug("deleteFilm: " + filmId);
        service.removeFilm(filmId);
    }
}
