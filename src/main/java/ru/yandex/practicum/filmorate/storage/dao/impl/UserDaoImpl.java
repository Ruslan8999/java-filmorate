package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Component("UserDaoImpl")
public class UserDaoImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAll() {
        final String sql = "SELECT * FROM users";
        Collection<User> users = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            users.add(new User(rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()));
        }
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        final String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (rs.next()) {
            User user = new User(rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate());
            user.setFriends((Set<User>) getUserFriends(user.getId()));
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(this.userToMap(user)).intValue());
        insertFriends(user);
        return user;
    }

    public Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        return values;
    }

    private void insertFriends(User user) {
        if (user.getFriends().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO friends (friend_id, user_id) VALUES (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (User friend : user.getFriends()) {
                ps.setInt(1, friend.getId());
                ps.setInt(2, user.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User update(User user) {
        if(findById(user.getId()).isEmpty()){
            throw new UnableToFindException();
        }
        final String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        deleteFriends(user);
        insertFriends(user);
        return user;
    }

    private void deleteFriends(User user) {
        final String sql = "DELETE FROM friends WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    @Override
    public Collection<User> getUserFriends(int id) {
        final String sqlFr = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        if (jdbcTemplate.queryForObject(sqlFr, Integer.class, id) == 0) {
            throw new ObjectNotFoundException("Данные не найдены");
        }
        final String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        Collection<User> friends = new HashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            friends.add(new User(rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()));
        }
        return friends;
    }

    @Override
    public Collection<User> getUserCrossFriends(int id, int otherId) {
        final String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id " +
                "FROM friends WHERE user_id = " + id + ") " +
                "AND user_id IN (SELECT friend_id FROM friends where user_id = " + otherId + ")";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()));
        return users;
    }

    @Override
    public void deleteUser(int id) {
        final String DELETE_FRIEND_BY_USER_ID = "DELETE FROM FRIENDS where USER_ID = ? ";
        jdbcTemplate.update(DELETE_FRIEND_BY_USER_ID, id);
        final String DELETE_FRIEND_BY_FRIEND_ID = "DELETE FROM FRIENDS where FRIEND_ID = ? ";
        jdbcTemplate.update(DELETE_FRIEND_BY_FRIEND_ID, id);
        final String DELETE_USER = "DELETE FROM USERS  where USER_ID = ? ";
        jdbcTemplate.update(DELETE_USER, id);
    }
}
