package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserStorage userStorage;

     Optional<User> findById(int id) {
        return userStorage.findById(id);
    }

    public Collection<Optional<User>> getFriends(int id) {
        return userStorage.getUserFriends(id);
    }

    public Collection<User> getUserCrossFriends(int id, int otherId){
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
