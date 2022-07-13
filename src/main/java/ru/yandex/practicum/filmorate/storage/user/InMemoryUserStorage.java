package ru.yandex.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;


@Component
public class InMemoryUserStorage implements UserStorage {
    private final Set<User> users = new HashSet<>();

    public User create(User user) {
        if (users.contains(user)) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " уже зарегистрирован.");
        } else {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.add(user);
            return user;
        }
    }

    public User update(User user) {
        if (!users.contains(user)) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден.");
        } else {
            users.remove(user);
            users.add(user);
            return user;
        }
    }

    public void delete(User user) {
        if (!users.contains(user)) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден для удаления.");
        } else {
            users.remove(user);
        }
    }

    public Set<User> findAll() {
        return users;
    }
}
