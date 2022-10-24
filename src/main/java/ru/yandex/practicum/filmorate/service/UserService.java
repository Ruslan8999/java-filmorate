package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDaoImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
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

    public Optional<User> createUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        userStorage.create(user);

        return Optional.of(user);
    }

    public User updateUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
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

    /*
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

     */
}
