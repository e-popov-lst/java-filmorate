package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class FilmController {
    private final ObjectMapper mapper =
            new ObjectMapper().registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private final FilmService filmService;
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;


    @Autowired
    public FilmController(FilmService filmService, InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @GetMapping("/films")
    public Set<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws JsonProcessingException {
        filmStorage.create(film);
        log.debug("Add film: {}", mapper.writeValueAsString(film));
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) throws JsonProcessingException {
        filmStorage.update(film);
        log.debug("Change film: {}", mapper.writeValueAsString(film));
        return film;
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable String id) {
        return filmStorage.findFilmById(Long.parseLong(id));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable String id, @PathVariable String userId) {
        Film film = filmStorage.findFilmById(Long.parseLong(id));
        User user = userStorage.findUserById(Long.parseLong(userId));

        filmService.addLike(film, user);
        return film;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable String id, @PathVariable String userId) {
        Film film = filmStorage.findFilmById(Long.parseLong(id));
        User user = userStorage.findUserById(Long.parseLong(userId));

        filmService.removeLike(film, user);
        return film;
    }

    @GetMapping("/films/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") String count) {
        return filmService.popularFilms(Integer.parseInt(count));
    }
}
