package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UnableToFindException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("User findAll");
        return userService.findAll();
    }

    @GetMapping("{id}")
    public Optional<User> findById(@PathVariable int id) {
        if (userService.findById(id).isEmpty()) {
            throw new UnableToFindException();
        }
        log.debug("User findById: " + id);
        return userService.findById(id);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        log.debug("User getFriends: " + id);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCrossFriend(@PathVariable int id, @PathVariable int otherId) {
        log.debug("User getCrossFriend");
        return userService.getUserCrossFriends(id, otherId);
    }


    @PostMapping
    public Optional<User> createUser(@Valid @RequestBody User user) {
        log.debug("createUser: " + user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("updateUser: " + user);
        return userService.updateUser(user);
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
