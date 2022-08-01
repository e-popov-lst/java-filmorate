package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    @Order(1)
    void findAll() throws Exception {
        userService.create(new User(101L, "123@test.com", "t123", "John", LocalDate.of(2004, 05, 22), null));
        userService.create(new User(102L, "1231@test.com", "t1231", "John2", LocalDate.of(2004, 05, 23), null));

        assertTrue(userService.findAll().size() >= 2);
    }

    @Test
    void create() throws Exception {
        User user = userService.create(new User(911L, "123@test.com", "t123", "John7", LocalDate.of(2004, 05, 22), null));
        User user2 = userService.findUserById(user.getId());

        assertEquals("John7", user2.getName());
    }

    @Test
    void update() throws Exception {
        User user = userService.create(new User(911L, "777@test.com", "t123", "John7", LocalDate.of(2004, 05, 22), null));
        User user2 = userService.update(new User(user.getId(), "555@test.com", "t123", "John7", LocalDate.of(2004, 05, 22), null));

        assertEquals("555@test.com", user2.getEmail());
    }
}

