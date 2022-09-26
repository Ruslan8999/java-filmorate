package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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
    public List<Film> getMostPopular(Integer count){
        return films.values().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            throw new ValidationException("Указываемая дата релиза не должна быть ранее 28.12.1895 года");
        }
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
}
