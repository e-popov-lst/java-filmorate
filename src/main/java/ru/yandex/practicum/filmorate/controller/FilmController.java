package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RestController
@Slf4j
public class FilmController {
    private final Set<Film> films = new HashSet<>();
    private final ObjectMapper mapper;

    public FilmController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/films")
    public Set<Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws JsonProcessingException {
        if (films.contains(film)) {
            throw new ValidationException("Фильм с id=" + film.getId() + " уже добавлен.");
        } else {
            validate(film);
            films.add(film);
            log.debug("Add film: {}", mapper.writeValueAsString(film));
            return film;
        }
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) throws JsonProcessingException {
        validate(film);
        films.add(film);
        log.debug("Change film: {}", mapper.writeValueAsString(film));
        return film;
    }

    public static void validate(Film film){
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза - не раньше 28 декабря 1895 года.");
        }
    }
}
