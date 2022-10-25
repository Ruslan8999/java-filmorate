package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("FilmDaoImpl") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Optional<Film> findById(int id) {
        return filmStorage.findById(id);
    }

    public Film createFilm(Film film){
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            throw new ValidationException("Указываемая дата релиза не должна быть ранее 28.12.1895 года");
        }
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Collection<Film>getMostPopular(Integer count, Integer genreId, Integer date){
        return filmStorage.getMostPopular(count, genreId, date);
    }

    public void addLike(int filmId, int userId) throws FilmNotFoundException {
        Film film = filmStorage.findById(filmId).get();
        User user = userService.findById(userId).get();
        film.addLike(userId);
        filmStorage.updateFilm(film);
        log.debug("Пользователь: {} поставил like фильму: {}", user, film);
    }

    public void removeLike(int filmId, int userId) {
        if (filmId < 1 || userId < 1) {
            throw new UnableToFindException();
        }
        Film film = filmStorage.findById(filmId).get();
        User user = userService.findById(userId).get();
        film.removeLike(userId);
        filmStorage.updateFilm(film);
        log.debug("Пользователь: {} удалил like фильму: {}", user, film);
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void removeFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }
}
