package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int idUserCounter = 1;
    private final Map<Integer, User> users = new HashMap();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(int id) {
        User user = users.get(id);
        if (user != null) {
            return Optional.of(users.get(id));
        } else {
            throw new UserNotFoundException(String.format("Пользователя с id: " + id + " не существует"));
        }
    }

    @Override
    public User create(User user) {
        if(user.getName() == null || user.getName().isEmpty()){
            user.setName(user.getLogin());
        }
        user.setId(idUserCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Введен некорректный id = " + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUserFriends(int id) {
        return users.get(id).getFriends();
    }

    @Override
    public Collection<User> getUserCrossFriends(int id, int otherId) {
        int userId = findById(id).get().getId();
        return findById(id).get().getFriends().stream()
                .filter(friend -> friend.getFriends().stream().map(user -> user.getId()).equals(userId))
                .collect(Collectors.toSet());

    }

    @Override
    public void deleteUser(int id) {
        throw new UnsupportedOperationException("В данной реализации хранилища метод не поддерживается");
    }
}
