package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    private int idUserCounter = 1;
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap();

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST /users");
        if(user.getName() == null){
            user.setName(user.getLogin());
        }
        user.setId(idUserCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) throws IOException {
        log.debug("Получен запрос PUT /users");
        if (!users.containsKey(user.getId())) {
            throw new IOException("Введен некорректный id = " + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }
}
