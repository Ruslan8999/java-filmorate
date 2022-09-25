package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();
    Optional<User> findById(int id);
    User create(User user);
    User update(User user);
    public Collection<User> getUserFriends(int id);
    Collection<User> getUserCrossFriends(int id, int otherId);

}
