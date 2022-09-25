package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Optional<User> findById(int id) {
        return userStorage.findById(id);
    }

    public Collection<User> getFriends(int id) {
        return userStorage.getUserFriends(id);
    }

    public Collection<User> getUserCrossFriends(int id, int otherId){
        return userStorage.getUserCrossFriends(id, otherId);
    }

    public void addFriend(int id, int friendId) {
        User friend = userStorage.findById(friendId).get();
        User user = userStorage.findById(id).get();
        user.addFriend(friend);
        userStorage.update(user);
    }

    public void removeFriend(int id, int userId) {
        User user = userStorage.findById(id).get();
        user.setFriends(user.getFriends().stream().filter(user1 -> user1.getId() != userId)
                .collect(Collectors.toSet()));
        userStorage.update(user);
    }
}
