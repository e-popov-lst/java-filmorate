package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    @Autowired
    private FilmService filmService;


    @Test
    @Order(1)
    void findAll() throws Exception {
        filmService.create(
                new Film(101L,
                        "Test film",
                        "Описание",
                        LocalDate.of(2004, 05, 21),
                        90,
                        1,
                        null,
                        null,
                        filmService.findRatingById(1L)));

        filmService.create(
                new Film(102L,
                        "Test film2",
                        "Описание2",
                        LocalDate.of(2004, 05, 22),
                        90,
                        2,
                        null,
                        null,
                        filmService.findRatingById(2L)));

        assertTrue(filmService.findAll().size() >= 2);
    }

    @Test
    void create() throws Exception {
        Film film = filmService.create(
                new Film(101L,
                        "Test film3",
                        "Описание",
                        LocalDate.of(2004, 05, 21),
                        90,
                        1,
                        null,
                        null,
                        filmService.findRatingById(1L)));

        Film film2 = filmService.findFilmById(film.getId());

        assertEquals("Test film3", film2.getName());
    }

    @Test
    void update() throws Exception {
        Film film = filmService.create(
                new Film(104L,
                        "Test film4",
                        "Описание4",
                        LocalDate.of(2004, 05, 24),
                        90,
                        4,
                        null,
                        null,
                        filmService.findRatingById(1L)));

        Film film2 = filmService.update(
                new Film(film.getId(),
                        "Test film5",
                        "Описание4",
                        LocalDate.of(2004, 05, 24),
                        90,
                        4,
                        null,
                        null,
                        filmService.findRatingById(1L)));

        assertEquals("Test film5", film2.getName());
    }
}
