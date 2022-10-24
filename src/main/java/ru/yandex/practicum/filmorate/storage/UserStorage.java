package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();
    Optional<User> findById(int id);
    User create(User user);
    User update(User user);
    Collection<User> getUserFriends(int id);
    Collection<User> getUserCrossFriends(int id, int otherId);
    void deleteUser(int id);
}
