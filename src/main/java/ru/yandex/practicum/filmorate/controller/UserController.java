package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class UserController {
    private final ObjectMapper mapper =
            new ObjectMapper().registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private final UserService userService;
    private final InMemoryUserStorage userStorage;
    private final Set<User> users = new HashSet<>();

    @Autowired
    public UserController(UserService userService, InMemoryUserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping("/users")
    public Set<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) throws JsonProcessingException {
        userStorage.create(user);
        log.debug("Add user: {}", mapper.writeValueAsString(user));
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) throws JsonProcessingException {
        userStorage.update(user);
        log.debug("Change user: {}", mapper.writeValueAsString(user));
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        return userStorage.findUserById(Long.parseLong(id));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable String id, @PathVariable String friendId) {
        User user = userStorage.findUserById(Long.parseLong(id));
        User friend = userStorage.findUserById(Long.parseLong(friendId));
        userService.addFriend(user, friend);

        return user;
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable String id, @PathVariable String friendId) {
        User user = userStorage.findUserById(Long.parseLong(id));
        User friend = userStorage.findUserById(Long.parseLong(friendId));
        userService.removeFriend(user, friend);

        return user;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable String id) {
        return userService.friends(userStorage.findUserById(Long.parseLong(id)));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.commonFriends(userStorage.findUserById(Long.parseLong(id)), userStorage.findUserById(Long.parseLong(otherId)));
    }
}
