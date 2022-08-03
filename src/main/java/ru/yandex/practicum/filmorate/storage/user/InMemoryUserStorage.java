package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Set<User> users = new HashSet<>();

    @Override
    public User create(User user) {
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

    @Override
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

    @Override
    public void delete(User user) {
        if (!users.contains(user)) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для удаления.");
        } else {
            users.remove(user);
        }
    }

    @Override
    public Set<User> findAll() {
        return users;
    }

    @Override
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

    @Override
    public User addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());

        return user;
    }

    @Override
    public User removeFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());

        return user;
    }

    @Override
    public List<User> getCommonFriends(User user1, User user2) {
        List<User> commonFriendsList = new ArrayList<>();

        if (user1.getFriendsId() != null) {
            Set<Long> intersection = new HashSet<>(user1.getFriendsId());
            intersection.retainAll(user2.getFriendsId());

            Iterator<Long> iterator = intersection.iterator();
            while (iterator.hasNext()) {
                commonFriendsList.add(findUserById(iterator.next()));
            }
        }

        return commonFriendsList;
    }

    @Override
    public List<User> getFriends(User user) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> friendsId = user.getFriendsId();

        Iterator<Long> iterator = friendsId.iterator();
        while (iterator.hasNext()) {
            friendsList.add(findUserById(iterator.next()));
        }

        return friendsList;
    }

    @Override
    public boolean isExistsUser(long userId) {
        try {
            users.stream().filter(data -> Objects.equals(data.getId(), userId)).findFirst().get();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
