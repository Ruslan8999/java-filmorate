package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage{
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
    public List<Optional<User>> getUserFriends(int id){
        Optional<User> user = findById(id);
        List<Optional<User>> result = new ArrayList<>();
        for (Integer idFriend: user.get().getFriends())
            result.add(findById(idFriend));
        return result;
    }

    @Override
    public ArrayList<User> getUserCrossFriends(int id, int otherId) {
        User user = users.get(id);
        User friend = users.get(otherId);
        return user.getCommonFriendList(friend).stream()
                .map(ids -> users.get(ids)).collect(Collectors.toCollection(ArrayList::new));
    }
}
