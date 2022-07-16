package ru.yandex.practicum.filmorate.storage.film;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.Film.MIN_RELEASE_DATE;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Set<Film> films = new HashSet<>();

    public Film create(Film film) {
        if (films.contains(film)) {
            throw new ValidationException("Фильм с id=" + film.getId() + " уже добавлен.");
        } else {
            validate(film);

            if (film.getId() == null) {
                film.setId();
            }

            films.add(film);
            log.debug("Add film: {}", film.toString());

            return film;
        }
    }

    public Film update(Film film) {
        if (!films.contains(film)) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для изменения.");
        } else {
            validate(film);
            films.remove(film);

            films.add(film);
            log.debug("Add film: {}", film.toString());

            return film;
        }
    }

    public void delete(Film film) {
        if (!films.contains(film)) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для удаления.");
        } else {
            films.remove(film);
        }
    }

    public Set<Film> findAll() {
        return films;
    }

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

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза - не раньше " +
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(new Locale("ru"))
                            .format(MIN_RELEASE_DATE));
        }
    }
}
