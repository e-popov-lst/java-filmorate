package ru.yandex.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static ru.yandex.practicum.filmorate.model.Film.MIN_RELEASE_DATE;


@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Set<Film> films = new HashSet<>();

    public Film create(Film film) {
        if (films.contains(film)) {
            throw new ValidationException("Фильм с id=" + film.getId() + " уже добавлен.");
        } else {
            validate(film);
            films.add(film);
            return film;
        }
    }

    public Film update(Film film) {
        if (!films.contains(film)) {
            throw new ValidationException("Фильм с id=" + film.getId() + " не найден.");
        } else {
            validate(film);
            films.remove(film);
            films.add(film);
            return film;
        }
    }

    public void delete(Film film) {
        if (!films.contains(film)) {
            throw new ValidationException("Фильм с id=" + film.getId() + " не найден для удаления.");
        } else {
            films.remove(film);
        }
    }

    public Set<Film> findAll() {
        return films;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза - не раньше " +
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(new Locale("ru"))
                            .format(MIN_RELEASE_DATE));
        }
    }

}
