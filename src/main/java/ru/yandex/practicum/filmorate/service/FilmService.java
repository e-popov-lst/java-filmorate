package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Film film, User user) {
        film.getLikesUserId().add(user.getId());
    }

    public void removeLike(Film film, User user) {
        film.getLikesUserId().remove(user.getId());
    }

    public List<Film> popularFilms(int count) {
        return filmStorage.findAll().stream()
                //.filter(f -> f.getLikesUserId().size() > 0)
                .sorted((f0, f1) -> compare(f0, f1))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return (f0.getLikesUserId().size() < f1.getLikesUserId().size() ? 1 : -1);
    }
}
