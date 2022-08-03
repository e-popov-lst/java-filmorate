package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;


@Service
public class UserService {
    public final UserStorage userStorage;


    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (user.getId() != null && userStorage.isExistsUser(user.getId())) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " уже зарегистрирован.");
        }

        return userStorage.create(user);
    }

    public User update(User user) {
        if (!userStorage.isExistsUser(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для изменения.");
        }

        return userStorage.update(user);
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public User addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User removeFriend(long userId, long friendId) {
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(User user1, User user2) {
        return userStorage.getCommonFriends(user1, user2);
    }

    public List<User> getFriends(User user) {
        return userStorage.getFriends(user);
    }
}
