package ru.yandex.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;


public interface FilmStorage {
    Film create(Film film);
    Film update(Film film);
    void delete(Film film);
}
