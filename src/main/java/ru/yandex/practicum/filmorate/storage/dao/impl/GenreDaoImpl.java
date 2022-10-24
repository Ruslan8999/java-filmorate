package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GenreDaoImpl implements GenreDao {
    private static final String SELECT_ALL = "SELECT * FROM genres";
    private static final String SELECT_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM films_genres WHERE genre_id = ?";
    private static final String ADD_GENRE = "INSERT INTO genres (name) values (?)";
    private static final String UPDATE_BY_ID = "UPDATE genres SET name = ? WHERE genre_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.queryForStream(SELECT_ALL, (rs, rowNum) ->
                new Genre(rs.getInt("GENRE_ID"), rs.getString("name"))).collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);
        if (rs.next()) {
            return Optional.of(new Genre(rs.getInt(1), rs.getString(2)));
        }
        return Optional.empty();
    }

    @Override
    public void deleteAllByFilmId(Long filmId) {
        jdbcTemplate.update(DELETE_BY_ID, filmId);
    }

    @Override
    public Optional<Genre> create(Genre genre) {
        if (jdbcTemplate.update(ADD_GENRE, genre.getName()) != 1) {
            return Optional.empty();
        } else {
            return Optional.of(genre);
        }
    }

    @Override
    public Optional<Genre> update(Genre genre) {
        if (jdbcTemplate.update(UPDATE_BY_ID, (genre.getId())) != 1) {
            return Optional.empty();
        } else {
            return Optional.of(genre);
        }
    }
}
