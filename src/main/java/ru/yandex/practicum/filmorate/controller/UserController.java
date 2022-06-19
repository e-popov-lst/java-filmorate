package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@Slf4j
public class UserController {
    private final Set<User> users = new HashSet<>();
    private final ObjectMapper mapper;

    public UserController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public Set<User> findAll() {
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) throws JsonProcessingException {
        if (users.contains(user)) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " уже зарегистрирован.");
        } else {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.add(user);
            log.debug("Add user: {}", mapper.writeValueAsString(user));
            return user;
        }
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) throws JsonProcessingException {
        if (!users.contains(user)) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден.");
        } else {
            users.remove(user);
            users.add(user);
            log.debug("Change user: {}", mapper.writeValueAsString(user));
            return user;
        }
    }
}
