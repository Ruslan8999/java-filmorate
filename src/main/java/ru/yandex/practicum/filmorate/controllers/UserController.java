package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Получен запрос GET /users");
        return userStorage.findAll();
    }

    @GetMapping("{id}")
    Optional<User> findById(@PathVariable int id){
        return userStorage.findById(id);
    }

    @GetMapping("{id}/friends")
    public Collection<Optional<User>> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCrossFriend(@PathVariable int id, @PathVariable int otherId){
        return userService.getUserCrossFriends(id, otherId);
    }


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST /users");
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws IOException {
        log.debug("Получен запрос PUT /users");
        return userStorage.update(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        if (id < 1 || friendId < 1) {
            throw new UnableToFindException();
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }
}
