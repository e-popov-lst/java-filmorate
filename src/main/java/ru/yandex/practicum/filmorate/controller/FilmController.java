package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
public class FilmController {
    private final FilmService filmService;


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Set<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        filmService.create(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        filmService.update(film);
        return film;
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable String id) {
        return filmService.findFilmById(Long.parseLong(id));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.addLike(Long.parseLong(id), Long.parseLong(userId));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.removeLike(Long.parseLong(id), Long.parseLong(userId));
    }

    @GetMapping("/films/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") String count) {
        return filmService.popularFilms(Integer.parseInt(count));
    }
}
