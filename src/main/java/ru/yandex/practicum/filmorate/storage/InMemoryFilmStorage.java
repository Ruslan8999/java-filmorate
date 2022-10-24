package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private int idFilmCounter = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(int id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        } else {
            throw new FilmNotFoundException("Введен некорректный id = " + id);
        }
    }

    @Override
    public List<Film> getMostPopular(Integer count, Integer genreId, Integer date) {

        if (genreId == null && date != null) {
            return films.values().stream()
                    .filter(f -> f.getReleaseDate().getYear() == date)
                    .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }

        if (genreId != null && date == null) {
            return films.values().stream()
                    .filter(f -> f.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId))
                    .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }

        if (genreId != null && date != null) {
            return films.values().stream()
                    .filter(f -> f.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId))
                    .filter(f -> f.getReleaseDate().getYear() == date)
                    .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }

        return films.values().stream().sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        film.setId(idFilmCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Введен некорректный id = " + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        throw new InternalServerException("Метод не используется в данной реализации");
    }

    @Override
    public void deleteFilm(int filmId) {
        throw new UnsupportedOperationException("Метод не используется в данной реализации");
    }
}
