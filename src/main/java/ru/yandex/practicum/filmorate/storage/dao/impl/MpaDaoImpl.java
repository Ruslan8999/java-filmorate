package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MpaDaoImpl implements MpaDao {
    private static final String SELECT_ALL = "SELECT * FROM mpa ORDER BY 1 ASC";
    private static final String SELECT_BY_ID = "SELECT * FROM mpa WHERE mpa_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.queryForStream(SELECT_ALL, (rs, rowNum) ->
                new Mpa(rs.getInt(1), rs.getString(2)))
                .sorted((o1, o2) -> o1.getId() < o2.getId() ? -1 : 1)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Mpa> getById(int id) {
        return jdbcTemplate.queryForStream(SELECT_BY_ID, (rs, rowNum) ->
                new Mpa(rs.getInt(1), rs.getString(2)), id).findFirst();
    }
}
