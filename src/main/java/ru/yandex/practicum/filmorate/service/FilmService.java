package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Set<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        userStorage.findUserById(userId);
        film.getLikesUserId().add(userId);

        return film;
    }

    public Film removeLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        userStorage.findUserById(userId);
        film.getLikesUserId().remove(userId);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f0, f1) -> compare(f0, f1))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return (f0.getLikesUserId().size() < f1.getLikesUserId().size() ? 1 : -1);
    }
}
