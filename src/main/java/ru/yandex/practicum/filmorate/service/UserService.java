package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Optional<User>> getFriends(int id) {
        return userStorage.getUserFriends(id);
    }

    public ArrayList<User> getUserCrossFriends(int id, int otherId){
        return userStorage.getUserCrossFriends(id, otherId);
    }

    public void addFriend(int id, int friendId) {
        User friend = userStorage.findById(friendId).get();
        User user = userStorage.findById(id).get();
        user.addFriend(friendId);
        userStorage.update(user);
        friend.addFriend(id);
        userStorage.update(friend);
    }

    public void removeFriend(int id, int friendId) {
        User friend = userStorage.findById(friendId).get();
        User user = userStorage.findById(id).get();
        user.removeFriend(friendId);
        userStorage.update(user);
        friend.removeFriend(id);
        userStorage.update(friend);
    }
}
