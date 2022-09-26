package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Optional<Film> findById(int id) {
        return filmStorage.findById(id);
    }

    public List<Film>getMostPopular(Integer count){
        return filmStorage.getMostPopular(count);
    }

    public void addLike(int filmId, int userId) throws FilmNotFoundException, IOException {
        Film film = filmStorage.findById(filmId).get();
        User user = userService.findById(userId).get();
        film.addLike(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь: {} поставил like фильму: {}", user, film);
    }

    public void removeLike(int filmId, int userId) {
        if (filmId < 1 || userId < 1) {
            throw new UnableToFindException();
        }
        Film film = filmStorage.findById(filmId).get();
        User user = userService.findById(userId).get();
        film.removeLike(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь: {} удалил like фильму: {}", user, film);
    }
}
