package ru.yandex.practicum.filmorate.storage.film;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.yandex.practicum.filmorate.model.Film;


public interface FilmStorage {
    Film create(Film film) throws JsonProcessingException;
    Film update(Film film) throws JsonProcessingException;
    void delete(Film film);
}
