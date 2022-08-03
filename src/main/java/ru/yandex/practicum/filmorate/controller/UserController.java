package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Set<User> findAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
        return userService.update(user);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        return userService.findUserById(Long.parseLong(id));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.addFriend(Long.parseLong(id), Long.parseLong(friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.removeFriend(Long.parseLong(id), Long.parseLong(friendId));
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable String id) {
        return userService.getFriends(userService.findUserById(Long.parseLong(id)));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.getCommonFriends(userService.findUserById(Long.parseLong(id)),
                userService.findUserById(Long.parseLong(otherId)));
    }
}
