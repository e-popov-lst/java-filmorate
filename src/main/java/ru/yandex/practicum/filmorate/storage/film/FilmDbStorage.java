package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }


    @Override
    public Film create(Film film) {
        if (film.getId() != null && isExistsFilm(film.getId())) {
            throw new ValidationException("Фильм с id=" + film.getId() + " уже добавлен.");
        } else {
            String sql = "INSERT INTO film(name, description, release_date, duration, rate, rating_id) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getRate());
                ps.setLong(6, film.getRatingMPA().getId());
                return ps;
            }, keyHolder);

            long filmId = (long) keyHolder.getKey();

            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    String sqlIns = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
                    jdbcTemplate.update(sqlIns, filmId, genre.getId());
                }
            }

            Film newFilm = findFilmById(filmId);
            log.debug("Add film: {}", newFilm);

            return newFilm;
        }
    }

    @Override
    public Film update(Film film) {
        if (!isExistsFilm(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для изменения.");
        } else {
            String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, rating_id = ? WHERE film_id = ?";

            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getRate(),
                    film.getRatingMPA().getId(),
                    film.getId());

            sql = "DELETE FROM film_genre WHERE film_id = ?";
            jdbcTemplate.update(sql, film.getId());

            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    sql = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            }

            Film newFilm = findFilmById(film.getId());
            log.debug("Change film: {}", newFilm);

            return newFilm;
        }
    }

    @Override
    public void delete(Film film) {
        if (!isExistsFilm(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для удаления.");
        } else {
            String sql = "DELETE FROM film WHERE film_id = ?";

            jdbcTemplate.update(sql, film.getId());
        }
    }

    @Override
    public Set<Film> findAll() {
        String sql = "SELECT f.film_id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.rate, " +
                "       f.rating_id, " +
                "       (SELECT listagg(fl.user_id, ',') WITHIN GROUP (ORDER BY fl.user_id) as likes_user_id " +
                "        FROM film_like fl " +
                "        WHERE fl.film_id = f.film_id) AS likes_user_id, " +
                "       (SELECT listagg(fg.genre_id, ',') WITHIN GROUP (ORDER BY fg.genre_id) as genres_id " +
                "        FROM film_genre fg " +
                "        WHERE fg.film_id = f.film_id) AS genres_id " +
                "FROM film f ";

        return new HashSet<>(jdbcTemplate.query(sql, this::makeFilm));
    }

    @Override
    public Film findFilmById(long id) {
        String sql = "SELECT f.film_id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.rate, " +
                "       f.rating_id, " +
                "       (SELECT listagg(fl.user_id, ',') WITHIN GROUP (ORDER BY fl.user_id) as likes_user_id " +
                "        FROM film_like fl " +
                "        WHERE fl.film_id = f.film_id) AS likes_user_id, " +
                "       (SELECT listagg(fg.genre_id, ',') WITHIN GROUP (ORDER BY fg.genre_id) as genres_id " +
                "        FROM film_genre fg " +
                "        WHERE fg.film_id = f.film_id) AS genres_id " +
                "FROM film f " +
                "WHERE f.film_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id=" + id + " не найден.");
        }
    }

    @Override
    public Film addLike(long filmId, long userId) {
        if (!isExistsFilm(filmId))
            throw new NotFoundException("Фильм с id=" + filmId + " не найден для добавления лайка.");
        if (!userStorage.isExistsUser(userId))
            throw new NotFoundException("Пользователь с id=" + userId + " не найден для добавления лайка фильму.");

        String sql = "INSERT INTO film_like(film_id, user_id) " +
                "SELECT ?, ? " +
                "MINUS " +
                "SELECT fl.film_id, fl.user_id " +
                "FROM film_like fl";

        jdbcTemplate.update(sql, filmId, userId);

        return findFilmById(filmId);
    }

    @Override
    public Film removeLike(long filmId, long userId) {
        if (!isExistsFilm(filmId))
            throw new NotFoundException("Фильм с id=" + filmId + " не найден для удаления лайка.");
        if (!userStorage.isExistsUser(userId))
            throw new NotFoundException("Пользователь с id=" + userId + " не найден для удаления лайка фильму.");

        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, filmId, userId);

        return findFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.film_id, " +
                "       max(f.name) AS name, " +
                "       max(f.description) AS description, " +
                "       max(f.release_date) AS release_date, " +
                "       max(f.duration) AS duration, " +
                "       max(f.rate) AS rate, " +
                "       max(f.rating_id) AS rating_id, " +
                "       listagg(fl.user_id, ',') WITHIN GROUP (ORDER BY fl.user_id) as likes_user_id, " +
                "       count(fl.user_id) AS likes_count, " +
                "       (SELECT listagg(fg.genre_id, ',') WITHIN GROUP (ORDER BY fg.genre_id) as genres_id " +
                "        FROM film_genre fg " +
                "        WHERE fg.film_id = f.film_id) AS genres_id " +
                "FROM film f " +
                "LEFT JOIN film_like fl ON fl.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private boolean isExistsFilm(long filmId) {
        String sql = "SELECT 1 FROM film f WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, filmId).size() > 0;
    }

    private Film makeFilm(ResultSet rs, int RowNum) throws SQLException {
        Set<Long> likesUserId = new HashSet<>();
        Set<Genre> genres = new HashSet<>();

        String strId = rs.getString("likes_user_id");
        if (strId != null) {
            String[] strLikesUserId = strId.split(",");
            for (String idStr : strLikesUserId) likesUserId.add(Long.valueOf(idStr));
        }

        strId = rs.getString("genres_id");
        if (strId != null) {
            String[] strGenresId = strId.split(",");
            for (String idStr : strGenresId) genres.add(findGenreById(Long.valueOf(idStr)));
        }

        return new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                rs.getInt("rate"),
                likesUserId,
                genres,
                findRatingById(rs.getLong("rating_id")));
    }


    @Override
    public Rating findRatingById(long id) {
        String sql = "SELECT r.rating_id, " +
                "       r.rating_value " +
                "FROM rating r " +
                "WHERE r.rating_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, this::makeRating, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с id=" + id + " не найден.");
        }
    }

    @Override
    public List<Rating> findAllRatings() {
        String sql = "SELECT r.rating_id, " +
                "       r.rating_value " +
                "FROM rating r ";

        return jdbcTemplate.query(sql, this::makeRating);
    }

    private Rating makeRating(ResultSet rs, int RowNum) throws SQLException {
        return new Rating(
                rs.getLong("rating_id"),
                rs.getString("rating_value")
        );
    }


    @Override
    public Genre findGenreById(long id) {
        String sql = "SELECT g.genre_id, " +
                "       g.name " +
                "FROM genre g " +
                "WHERE g.genre_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id=" + id + " не найден.");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "SELECT g.genre_id, " +
                "       g.name " +
                "FROM genre g " +
                "ORDER BY g.genre_id";

        return jdbcTemplate.query(sql, this::makeGenre);
    }

    private Genre makeGenre(ResultSet rs, int RowNum) throws SQLException {
        return new Genre(
                rs.getLong("genre_id"),
                rs.getString("name")
        );
    }
}
