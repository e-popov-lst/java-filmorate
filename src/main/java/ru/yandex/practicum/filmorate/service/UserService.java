package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;


@Service
public class UserService {
    InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(User user, User friend) {
        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());
    }

    public void removeFriend(User user, User friend) {
        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());
    }

    public List<User> commonFriends(User user1, User user2) {
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

    public List<User> friends(User user) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> friendsId = user.getFriendsId();

        Iterator<Long> iterator = friendsId.iterator();
        while (iterator.hasNext()) {
            friendsList.add(userStorage.findUserById(iterator.next()));
        }

        return friendsList;
    }
}
