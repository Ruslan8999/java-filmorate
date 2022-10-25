package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class FilmGenreDaoImpl implements FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Genre> findAllByFilmId(int id) {
        Set<Genre> genres = new HashSet<>();
        String sql = "SELECT * FROM films_genres G JOIN genres G2 ON G2.genre_id = G.genre_id " + "WHERE film_id = ? ORDER BY  1 ASC";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            genres.add(new Genre(rs.getInt("genre_id"),
                    rs.getString("name")));
        }
        return genres;
    }

    @Override
    public void addNewGenreToFilm(int filmId, Genre genre) {
        String sql = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genre.getId());
    }

    @Override
    public void updateAllGenreByFilm(Film film) {
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) {
                deleteAll(film);
                return;
            }
            deleteAll(film);
            StringBuilder builder = new StringBuilder("INSERT INTO films_genres(genre_id, film_id) VALUES ");
            film.setGenres(film.getGenres().stream()
                    .distinct()
                    .collect(Collectors.toList()));
            for (Genre genre : film.getGenres()) {
                builder.append("(" + genre.getId() + ",")
                        .append(film.getId() + "),");
            }
            String sql = builder.subSequence(0, builder.length() - 1).toString();
            jdbcTemplate.update(sql);
        }
    }

    private void deleteAll(Film film) {
        final String sql = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }
}
