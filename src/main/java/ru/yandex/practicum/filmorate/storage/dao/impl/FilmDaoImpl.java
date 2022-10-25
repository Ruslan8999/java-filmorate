package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("FilmDaoImpl")
public class FilmDaoImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private static final String SELECT_ALL =
            "SELECT FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, " +
            "FILMS.DURATION, FILMS.MPA_ID, FILMS_GENRES.GENRE_ID, GENRES.NAME AS GNAME, MPA.NAME AS MPA_NAME " +
            "FROM FILMS " +
            "JOIN FILMS_GENRES ON films.FILM_ID = FILMS_GENRES.FILM_ID " +
            "JOIN GENRES ON FILMS_GENRES.GENRE_ID = GENRES.GENRE_ID " +
            "JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID ORDER BY FILM_ID";

    private static final String SELECT_BY_ID =
            "SELECT f.FILM_ID AS FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, " +
            "M2.NAME AS MPA_NAME, fg.GENRE_ID AS GID, G2.NAME AS GNAME, L.USER_ID AS `LIKE` FROM films f  " +
            "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRES AS G2 ON fg.GENRE_ID = G2.GENRE_ID " +
            "LEFT JOIN LIKES L ON f.FILM_ID = L.FILM_ID " +
            "LEFT JOIN MPA M2 ON f.MPA_ID = M2.MPA_ID " +
            "WHERE f.FILM_ID = ?";

    private static final String SEL_COMMON_FILMS_SQL =
            "SELECT f.film_id FROM likes l1 " +
                    "LEFT JOIN likes l2 ON l1.film_id = l2.film_id " +
                    "LEFT JOIN films f ON l1.film_id = f.film_id " +
                    "WHERE l1.user_id=? AND l2.user_id=? AND l1.film_id=l2.film_id";

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_ALL);
        while (rs.next()) {
            Film film = new Film(rs.getInt("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")));
            Set<Genre> genres = new HashSet<>();
                    genres.add(new Genre(rs.getInt("GENRE_ID"), rs.getString("GNAME")));
            film.setGenres(genres.stream()
                    .collect(Collectors.toList()));
            films.add(film);
        }
        return films;
    }

    @Override
    public Optional<Film> findById(int id) {
        final String sqlFirst = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ?";
        if (jdbcTemplate.queryForObject(sqlFirst, Integer.class, id) == 0) {
            throw new ObjectNotFoundException("Данные не найдены");
        }
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);
        if (rs.next()) {
            Set<Genre> genres = new HashSet<>();
            Set<Integer> likes = new HashSet<>();

            Film film = null;

            film = Film.builder()
                    .description(rs.getString("description"))
                    .id(rs.getInt("film_id"))
                    .name(rs.getString("NAME"))
                    .releaseDate((rs.getDate("release_date").toLocalDate()))
                    .duration(rs.getInt("duration"))
                    .build();

            film.setMpa(new Mpa(rs.getInt("MPA_ID"),
                    rs.getString("MPA_NAME")));

            do {
                if (rs.getString("GNAME") != null) {
                    genres.add(new Genre(rs.getInt("GID"), rs.getString("GNAME")));
                }
                int res;
                if ((res = rs.getInt("LIKE")) != 0) {
                    likes.add(res);
                }
            } while (rs.next());

            film.setGenres(genres.stream()
                    .collect(Collectors.toList()));
            film.setLikes(likes);
            getLikesByFilm(film);
            if (film.getGenres().isEmpty()) {
                film.setGenres(null);
            }
            return Optional.of(film);
        }
        return Optional.empty();
    }

    private void getLikesByFilm(Film film) {
        final String sql = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Integer> likes = jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getInt("USER_ID"), film.getId());
        film.getLikes().addAll(likes);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(this.filmToMap(film)).intValue());

        filmGenreDao.updateAllGenreByFilm(film);

        insertLikes(film);
        return film;
    }

    private void insertLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO LIKES(USER_ID, FILM_ID) VALUES (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Integer like : film.getLikes()) {
                ps.setInt(1, like);
                ps.setInt(2, film.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        if (film.getMpa() != null) {
            values.put("MPA_ID", film.getMpa().getId());
        }
        return values;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() != 0 && film.getId() < 1) {
            throw new UnableToFindException();
        }
        final String sqlFirst = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ?";
        if (jdbcTemplate.queryForObject(sqlFirst, Integer.class, film.getId()) == 0) {
            throw new ObjectNotFoundException("Данные не найдены");
        }

        final String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?,RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
        int count = jdbcTemplate.update(sql
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());

        if (count == 1) {
            filmGenreDao.updateAllGenreByFilm(film);
            updateLikes(film);
        }
        return film;
    }

    private void updateLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            deleteLikes(film);
            return;
        }
        deleteLikes(film);
        insertLikes(film);
    }

    private void deleteLikes(Film film) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public Collection<Film> getMostPopular(Integer count, Integer genreId, Integer date) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPA_ID, F.NAME AS MPA_NAME " +
                "FROM FILMS  F LEFT JOIN  LIKES L ON F.FILM_ID  = L.FILM_ID " +
                "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";

        if (genreId == null && date != null) {
            sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPA_ID, F.NAME AS MPA_NAME " +
                    "FROM FILMS  F LEFT JOIN  LIKES L ON F.FILM_ID  = L.FILM_ID " +
                    "WHERE EXTRACT(YEAR FROM RELEASE_DATE) = :date " +
                    "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";
        }

        if (genreId != null && date == null) {
            sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPAA_ID, F.NAME AS MPA_NAME" +
                    ", FG.GENRE_ID " +
                    "FROM FILMS  F " +
                    "LEFT JOIN  LIKES L ON F.FILM_ID  = L.FILM_ID " +
                    "LEFT JOIN FILMS_GENRES FG ON F.FILM_ID = FG.FILM_ID " +
                    "WHERE FG.GENRE_ID = :genreId " +
                    "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";
        }

        if (genreId != null && date != null) {
            sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPA_ID, F.NAME AS MPA_NAME " +
                    "FROM FILMS  F " +
                    "LEFT JOIN  LIKES L ON F.FILM_ID  = L.FILM_ID " +
                    "LEFT JOIN FILMS_GENRES FG ON F.FILM_ID = FG.FILM_ID " +
                    "WHERE FG.GENRE_ID = :genreId AND EXTRACT(YEAR FROM RELEASE_DATE) = :date " +
                    "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";
        }

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("count", count)
                .addValue("genreId", genreId)
                .addValue("date", date);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Film> films = namedParameterJdbcTemplate.query(sql, sqlParameterSource, (rs, rowNum) ->
                {
                    Film film = new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            (rs.getInt("duration")),
                            new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME"))
                    );
                    getLikesByFilm(film);
                    if (filmGenreDao.findAllByFilmId(film.getId()).isEmpty()) {
                        film.setGenres(null);
                    } else {
                        film.setGenres(new ArrayList<>(filmGenreDao.findAllByFilmId(film.getId())));
                    }
                    return film;
                }
        );
        return films;
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return jdbcTemplate.query(SEL_COMMON_FILMS_SQL, this::mapFilm, userId, friendId);
    }

    private Film mapFilm(ResultSet row, int rowNum) throws SQLException {
        return findById(row.getInt("film_id")).get();
    }

    @Override
    public void deleteFilm(int filmId) {
        final String sql = "DELETE FROM FILMS where FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
