package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public User create(User user) {
        if (user.getId() != null && isExistsUser(user.getId())) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " уже зарегистрирован.");
        } else {
            String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            long userId = (long) keyHolder.getKey();

            User newUser = findUserById(userId);
            log.debug("Add user: {}", newUser);

            return newUser;
        }
    }

    @Override
    public User update(User user) {
        if (!isExistsUser(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для изменения.");
        } else {
            String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId());

            User newUser = findUserById(user.getId());
            log.debug("Change user: {}", newUser);

            return newUser;
        }
    }

    @Override
    public void delete(User user) {
        if (!isExistsUser(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для удаления.");
        } else {
            String sql = "DELETE FROM users WHERE user_id = ?";

            jdbcTemplate.update(sql, user.getId());
        }
    }

    @Override
    public Set<User> findAll() {
        String sql = "SELECT u.user_id," +
                "       max(u.email) as email," +
                "       max(u.login) as login," +
                "       max(u.name) as name," +
                "       max(u.birthday) as birthday," +
                "       listagg(f.candidate, ',') WITHIN GROUP (ORDER BY f.friend_id) as friendsId " +
                "FROM users u " +
                "LEFT JOIN friends f ON f.initiator = u.user_id AND f.is_confirmed = TRUE " +
                "GROUP BY u.user_id";

        return new HashSet<>(jdbcTemplate.query(sql, this::makeUser));
    }

    @Override
    public User findUserById(long id) {
        String sql = "SELECT u.user_id," +
                "       max(u.email) as email," +
                "       max(u.login) as login," +
                "       max(u.name) as name," +
                "       max(u.birthday) as birthday," +
                "       listagg(f.candidate, ',') WITHIN GROUP (ORDER BY f.friend_id) as friendsId " +
                "FROM users u " +
                "LEFT JOIN friends f ON f.initiator = u.user_id AND f.is_confirmed = TRUE " +
                "WHERE u.user_id = ? " +
                "GROUP BY u.user_id";

        try {
            return jdbcTemplate.queryForObject(sql, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }
    }

    @Override
    public User addFriend(long userId, long friendId) {
        if (!isExistsUser(userId))
            throw new NotFoundException("Пользователь с id=" + userId + " не найден для добавления друга.");
        if (!isExistsUser(friendId))
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден для добавления в качестве друга.");

        String sql = "INSERT INTO friends(initiator, candidate, is_confirmed) " +
                "SELECT ?, ?, TRUE  " +
                "WHERE NOT EXISTS (" +
                "  SELECT 1 " +
                "  FROM friends f " +
                "  WHERE f.is_confirmed = TRUE " +
                "    AND f.initiator = ? " +
                "    AND f.candidate = ? " +
                ")";

        jdbcTemplate.update(sql, userId, friendId, userId, friendId);

        return findUserById(userId);
    }

    @Override
    public User removeFriend(long userId, long friendId) {
        if (!isExistsUser(userId))
            throw new NotFoundException("Пользователь с id=" + userId + " не найден для удаления друга.");
        if (!isExistsUser(friendId))
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден для удаления из друзей.");

        String sql = "DELETE FROM friends " +
                "WHERE initiator = ? AND candidate = ? ";

        jdbcTemplate.update(sql, userId, friendId);

        return findUserById(userId);
    }

    @Override
    public List<User> getCommonFriends(User user1, User user2) {
        if (!isExistsUser(user1.getId()))
            throw new NotFoundException("Пользователь с id=" + user1.getId() + " не найден для поиска общих друзей со вторым пользователем.");
        if (!isExistsUser(user2.getId()))
            throw new NotFoundException("Пользователь с id=" + user2.getId() + " не найден для поиска общих друзей с первым пользователем.");

        String sql = "SELECT u.user_id," +
                "       max(u.email) as email," +
                "       max(u.login) as login," +
                "       max(u.name) as name," +
                "       max(u.birthday) as birthday," +
                "       listagg(f.candidate, ',') WITHIN GROUP (ORDER BY f.friend_id) as friendsId " +
                "FROM users u " +
                "LEFT JOIN friends f ON f.initiator = u.user_id AND f.is_confirmed = TRUE " +
                "WHERE EXISTS(" +
                "        SELECT 1 " +
                "        FROM friends f1 " +
                "        WHERE f1.is_confirmed = TRUE " +
                "          AND f1.candidate = u.user_id " +
                "          AND f1.initiator = ? " +
                "      )" +
                "  AND EXISTS(" +
                "        SELECT 1 " +
                "        FROM friends f1 " +
                "        WHERE f1.is_confirmed = TRUE " +
                "          AND f1.candidate = u.user_id " +
                "          AND f1.initiator = ? " +
                "      )" +
                "GROUP BY u.user_id";

        List<User> commonFriendsList = jdbcTemplate.query(sql, this::makeUser, user1.getId(), user2.getId());

        return commonFriendsList;
    }

    @Override
    public List<User> getFriends(User user) {
        if (!isExistsUser(user.getId()))
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден для поиска друзей.");

        String sql = "SELECT u.user_id, " +
                "       max(u.email) as email, " +
                "       max(u.login) as login, " +
                "       max(u.name) as name, " +
                "       max(u.birthday) as birthday, " +
                "       listagg(f.candidate, ',') WITHIN GROUP (ORDER BY f.friend_id) as friendsId " +
                "FROM users u " +
                "LEFT JOIN friends f ON f.initiator = u.user_id AND f.is_confirmed = TRUE " +
                "WHERE EXISTS( " +
                "        SELECT 1 " +
                "        FROM friends f1 " +
                "        WHERE f1.is_confirmed = TRUE " +
                "          AND f1.candidate = u.user_id " +
                "          AND f1.initiator =  ? " +
                "      ) " +
                "GROUP BY u.user_id";

        List<User> commonFriendsList = jdbcTemplate.query(sql, this::makeUser, user.getId());

        return commonFriendsList;
    }

    public boolean isExistsUser(long userId) {
        String sql = "SELECT 1 FROM users u WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, userId).size() > 0;
    }

    private User makeUser(ResultSet rs, int RowNum) throws SQLException {
        Set<Long> friendsId = new HashSet<>();
        String strId = rs.getString("friendsId");
        if (strId != null) {
            String[] strFriendsid = strId.split(",");
            for (String idStr : strFriendsid) friendsId.add(Long.valueOf(idStr));
        }

        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                friendsId
        );
    }
}
