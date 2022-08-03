package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Set<Film> films = new HashSet<>();
    private final InMemoryUserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }


    @Override
    public Film create(Film film) {
        if (films.contains(film)) {
            throw new ValidationException("Фильм с id=" + film.getId() + " уже добавлен.");
        } else {
            if (film.getId() == null) {
                film.setId();
            }

            films.add(film);
            log.debug("Add film: {}", film);

            return film;
        }
    }

    @Override
    public Film update(Film film) {
        if (!films.contains(film)) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для изменения.");
        } else {
            films.remove(film);

            films.add(film);
            log.debug("Change film: {}", film);

            return film;
        }
    }

    @Override
    public void delete(Film film) {
        if (!films.contains(film)) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для удаления.");
        } else {
            films.remove(film);
        }
    }

    @Override
    public Set<Film> findAll() {
        return films;
    }

    @Override
    public Film findFilmById(long id) {
        Film film;

        try {
            film = films.stream().filter(data -> Objects.equals(data.getId(), id)).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Фильм с id=" + id + " не найден.");
        }

        if (film == null) throw new NotFoundException("Фильм с id=" + id + " не найден.");

        return film;
    }

    @Override
    public Film addLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        userStorage.findUserById(userId);
        film.getLikesUserId().add(userId);

        return film;
    }

    @Override
    public Film removeLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        userStorage.findUserById(userId);
        film.getLikesUserId().remove(userId);

        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return findAll().stream()
                .sorted((f0, f1) -> compare(f0, f1))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return (f0.getLikesUserId().size() < f1.getLikesUserId().size() ? 1 : -1);
    }

    @Override
    public Rating findRatingById(long id) {
        return null;
    }

    @Override
    public List<Rating> findAllRatings() {
        return null;
    }

    @Override
    public Genre findGenreById(long id) {
        return null;
    }

    @Override
    public List<Genre> findAllGenres() {
        return null;
    }

    @Override
    public boolean isExistsFilm(long filmId) {
        try {
            films.stream().filter(data -> Objects.equals(data.getId(), filmId)).findFirst().get();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
