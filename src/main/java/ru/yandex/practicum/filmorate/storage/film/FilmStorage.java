package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Set;


public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void delete(Film film);

    Set<Film> findAll();

    Film findFilmById(long id);

    Film addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);

    Rating findRatingById(long id);

    List<Rating> findAllRatings();

    Genre findGenreById(long id);

    List<Genre> findAllGenres();
}
