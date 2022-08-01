package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.Film.MIN_RELEASE_DATE;


@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Set<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        return filmStorage.update(film);
    }

    public Film findFilmById(long id) { return filmStorage.findFilmById(id); }

    public Film addLike(long filmId, long userId) { return filmStorage.addLike(filmId, userId); }

    public Film removeLike(long filmId, long userId) { return filmStorage.removeLike(filmId, userId); }

    public List<Film> getPopularFilms(int count) { return filmStorage.getPopularFilms(count); }

    public Rating findRatingById(long id) { return filmStorage.findRatingById(id); }

    public List<Rating> findAllRatings() { return filmStorage.findAllRatings(); }

    public Genre findGenreById(long id) { return filmStorage.findGenreById(id); }

    public List<Genre> findAllGenres() { return filmStorage.findAllGenres(); }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза - не раньше " +
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(new Locale("ru"))
                            .format(MIN_RELEASE_DATE));
        }
    }
}
