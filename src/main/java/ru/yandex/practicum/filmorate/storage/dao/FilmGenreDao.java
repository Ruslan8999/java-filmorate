package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Repository
public interface FilmGenreDao {
    Set<Genre> findAllByFilmId(int id);

    void addNewGenreToFilm(int filmId, Genre genre);

    void updateAllGenreByFilm(Film film);
}
