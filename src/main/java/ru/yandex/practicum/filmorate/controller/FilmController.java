package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
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
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") String count) {
        return filmService.getPopularFilms(Integer.parseInt(count));
    }

    @GetMapping(value = "/genres")
    public List<Genre> findAllGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenre(@PathVariable String id) {
        return filmService.findGenreById(Long.parseLong(id));
    }

    @GetMapping(value = "/mpa")
    public List<Rating> findAllRatings() {
        return filmService.findAllRatings();
    }

    @GetMapping(value = "/mpa/{id}")
    public Rating getRating(@PathVariable String id) {
        return filmService.findRatingById(Long.parseLong(id));
    }
}
