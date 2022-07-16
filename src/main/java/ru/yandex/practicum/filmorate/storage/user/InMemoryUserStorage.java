package ru.yandex.practicum.filmorate.storage.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;


@Slf4j
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

            if (user.getId() == null) {
                user.setId();
            }

            users.add(user);
            log.debug("Add user: {}", user);

            return user;
        }
    }

    public User update(User user) {
        if (!users.contains(user)) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для изменения.");
        } else {
            users.remove(user);

            users.add(user);
            log.debug("Change user: {}", user);

            return user;
        }
    }

    public void delete(User user) {
        if (!users.contains(user)) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для удаления.");
        } else {
            users.remove(user);
        }
    }

    public Set<User> findAll() {
        return users;
    }

    public User findUserById(long id) {
        User user;

        try {
            user = users.stream().filter(data -> Objects.equals(data.getId(), id)).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }

        if (user == null) throw new NotFoundException("Пользователь с id=" + id + " не найден.");

        return user;
    }
}
