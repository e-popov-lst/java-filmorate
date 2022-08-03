package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;


public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(User user);

    Set<User> findAll();

    User findUserById(long id);

    User addFriend(long userId, long friendId);

    User removeFriend(long userId, long friendId);

    List<User> getCommonFriends(User user1, User user2);

    List<User> getFriends(User user);

    boolean isExistsUser(long userId);
}
