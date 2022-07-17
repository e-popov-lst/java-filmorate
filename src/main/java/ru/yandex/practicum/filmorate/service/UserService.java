package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;


@Service
public class UserService {
    private final InMemoryUserStorage userStorage;


    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());

        return user;
    }

    public User removeFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());

        return user;
    }

    public List<User> getCommonFriends(User user1, User user2) {
        List<User> commonFriendsList = new ArrayList<>();

        if (user1.getFriendsId() != null) {
            Set<Long> intersection = new HashSet<>(user1.getFriendsId());
            intersection.retainAll(user2.getFriendsId());

            Iterator<Long> iterator = intersection.iterator();
            while (iterator.hasNext()) {
                commonFriendsList.add(userStorage.findUserById(iterator.next()));
            }
        }

        return commonFriendsList;
    }

    public List<User> getFriends(User user) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> friendsId = user.getFriendsId();

        Iterator<Long> iterator = friendsId.iterator();
        while (iterator.hasNext()) {
            friendsList.add(userStorage.findUserById(iterator.next()));
        }

        return friendsList;
    }
}
