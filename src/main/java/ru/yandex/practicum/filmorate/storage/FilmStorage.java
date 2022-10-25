package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();
    Optional<Film> findById(int id);
    Film create(Film film);
    Film updateFilm(Film film);
    Collection<Film> getMostPopular(Integer count, Integer genreId, Integer date);
    Collection<Film> getCommonFilms(int userId, int friendId);
    void deleteFilm(int filmId);
}
